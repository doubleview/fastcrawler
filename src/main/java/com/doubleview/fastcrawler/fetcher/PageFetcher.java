package com.doubleview.fastcrawler.fetcher;

import com.doubleview.fastcrawler.CrawlerConfig;
import com.doubleview.fastcrawler.CrawlerConfiguable;
import com.doubleview.fastcrawler.CrawlerRequest;
import com.doubleview.fastcrawler.Page;
import com.doubleview.fastcrawler.exceptions.ContentFetchException;
import com.doubleview.fastcrawler.exceptions.PageSizeOverException;
import com.doubleview.fastcrawler.utils.UrlUtils;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.HttpStatus;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.concurrent.locks.ReentrantLock;
/**
 *
 *
 */
public class PageFetcher extends CrawlerConfiguable{

    protected static final Logger logger = LoggerFactory.getLogger(PageFetcher.class);

    protected final ReentrantLock lock = new ReentrantLock();

    protected PoolingHttpClientConnectionManager connectionManager;

    protected CloseableHttpClient httpClient;

    protected  IdleConnectionMonitorThread connectionMonitorThread = null;

    protected  long lastFetchTime = 0;

    public PageFetcher(CrawlerConfig crawlerConfig){
        super(crawlerConfig);
        initHttpClient(crawlerConfig);
    }


    /**
     * init the httpclient
     * @param crawlerConfig
     */
    private void initHttpClient(CrawlerConfig crawlerConfig){
        RequestConfig requestConfig = RequestConfig.custom()
                .setExpectContinueEnabled(true)
                .setCookieSpec(CookieSpecs.STANDARD)
                .setRedirectsEnabled(false)
                .setSocketTimeout(crawlerConfig.getSocketTimeout())
                .setConnectTimeout(crawlerConfig.getConnectionTimeout())
                .build();

        RegistryBuilder<ConnectionSocketFactory> connRegistryBuilder  = RegistryBuilder.create();
        connRegistryBuilder.register("http", PlainConnectionSocketFactory.INSTANCE);
        SSLConnectionSocketFactory sslConnectionSocketFactory = null;
        try {
            sslConnectionSocketFactory = new SSLConnectionSocketFactory(createIgnoreVerifySSL());
        } catch (Exception e) {
            logger.error("Error occurred : {}", e);
        }
        connRegistryBuilder.register("https", sslConnectionSocketFactory);
        Registry<ConnectionSocketFactory> connRegistry = connRegistryBuilder.build();
        connectionManager = new SniPoolingHttpClientConnectionManager(connRegistry);
        connectionManager.setMaxTotal(crawlerConfig.getMaxTotalConns());
        connectionManager.setDefaultMaxPerRoute(crawlerConfig.getMaxConnsPerHost());

        HttpClientBuilder clientBuilder = HttpClientBuilder.create()
                .setDefaultRequestConfig(requestConfig)
                .setConnectionManager(connectionManager)
                .setUserAgent(crawlerConfig.getUserAgent());

        if (crawlerConfig.getProxyHost() != null) {
            if (crawlerConfig.getProxyUsername() != null) {
                BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
                credentialsProvider.setCredentials(
                        new AuthScope(crawlerConfig.getProxyHost() , crawlerConfig.getProxyPort()) ,
                        new UsernamePasswordCredentials(crawlerConfig.getProxyUsername() , crawlerConfig.getProxyPassword()));
                clientBuilder.setDefaultCredentialsProvider(credentialsProvider);
            }
            HttpHost proxy = new HttpHost(crawlerConfig.getProxyHost(), crawlerConfig.getProxyPort());
            clientBuilder.setProxy(proxy);
            logger.debug("Working through Proxy : {}" , proxy.getHostName());
        }

        httpClient = clientBuilder.build();
        if (connectionMonitorThread == null) {
            connectionMonitorThread = new IdleConnectionMonitorThread(connectionManager);
        }
        connectionMonitorThread.start();
    }

    private SSLContext createIgnoreVerifySSL() throws NoSuchAlgorithmException, KeyManagementException {
        SSLContext sc = SSLContext.getInstance("SSLv3");

        // 实现一个X509TrustManager接口，用于绕过验证，不用修改里面的方法
        X509TrustManager trustManager = new X509TrustManager() {
            @Override
            public void checkClientTrusted(java.security.cert.X509Certificate[] paramArrayOfX509Certificate,
                                           String paramString) throws CertificateException {
            }

            @Override
            public void checkServerTrusted(java.security.cert.X509Certificate[] paramArrayOfX509Certificate,
                                           String paramString) throws CertificateException {
            }

            @Override
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null;
            }
        };

        sc.init(null, new TrustManager[] { trustManager }, null);
        return sc;
    }

    /**
     *
     * @param crawlerRequest
     * @return
     * @throws InterruptedException
     * @throws IOException
     */
    public FetchResult fetchPage(CrawlerRequest crawlerRequest) throws InterruptedException, IOException, PageSizeOverException {
        FetchResult fetchResult = new FetchResult();
        String toFetchURL = crawlerRequest.getUrl();
        fetchResult.setFetchUrl(toFetchURL);

        HttpUriRequest request = null;
        try {
            request = new HttpGet(toFetchURL);
            try {
                lock.lock();
                long now = System.currentTimeMillis();
                if( (now - lastFetchTime) < crawlerConfig.getFetchTimeDelay()){
                    Thread.sleep(crawlerConfig.getFetchTimeDelay() - (now - lastFetchTime));
                }
                lastFetchTime = System.currentTimeMillis();
            }finally {
                lock.unlock();
            }
            CloseableHttpResponse response = httpClient.execute(request);
            fetchResult.setEntity(response.getEntity());
            fetchResult.setResponseHeaders(response.getAllHeaders());

            int statusCode = response.getStatusLine().getStatusCode();
            fetchResult.setStatusCode(statusCode);

            if (statusCode == HttpStatus.SC_MOVED_PERMANENTLY ||
                    statusCode == HttpStatus.SC_MOVED_TEMPORARILY ||
                    statusCode == HttpStatus.SC_MULTIPLE_CHOICES ||
                    statusCode == HttpStatus.SC_SEE_OTHER ||
                    statusCode == HttpStatus.SC_TEMPORARY_REDIRECT || statusCode == 308) {
                Header header = response.getFirstHeader("Location");
                if (header != null) {
                    String movedToUrl = UrlUtils.canonicalizeUrl(header.getValue(), toFetchURL);
                    fetchResult.setMoveToUrl(movedToUrl);
                }else {
                    logger.warn("Unexpected error, URL: {} is redirected to NOTHING", crawlerRequest.getUrl());
                    return null;
                }
            } else if (statusCode >= 200 && statusCode <= 299) {
                if (fetchResult.getEntity() != null) {
                    long size = fetchResult.getEntity().getContentLength();
                    if (size == -1) {
                        Header length = response.getLastHeader("Content-Length");
                        if (length == null) {
                            length = response.getLastHeader("Content-length");
                        }
                        if (length != null) {
                            size = Integer.parseInt(length.getValue());
                        }
                    }
                    if (size > crawlerConfig.getMaxDownloadSize()) {
                        response.close();
                        throw new PageSizeOverException(size);
                    }
                }
            }
            return fetchResult;
        }finally {
            if (fetchResult.getEntity() == null && request != null) {
                request.abort();
            }
        }
    }


    /**
     *
     * @param fetchResult
     * @return
     */
    public Page loadPage(FetchResult fetchResult , CrawlerRequest request) throws ContentFetchException {
        try {
            Page page = new Page(request);
            if(fetchResult.getMoveToUrl() != null){
                page.setRedirect(true);
                CrawlerRequest redirectRequest = new CrawlerRequest(fetchResult.getMoveToUrl());
                redirectRequest.setParentUrl(fetchResult.getMoveToUrl());
                redirectRequest.setDepth(request.getDepth());
                page.setRedirectRequest(redirectRequest);
                return page;
            }
            try {
                page.load(fetchResult.getEntity() , crawlerConfig.getMaxDownloadSize());
                if (page.isTruncated()) {
                    logger.warn("Warning: unknown page size exceeded max-download-size, truncated to: " + "({}), at URL: {}", crawlerConfig.getMaxDownloadSize(), request.getUrl());
                }
            } catch (Exception e) {
                logger.info("Exception while fetching content for: {} [{}]", request.getUrl(), e.getMessage());
                throw new ContentFetchException(e);
            }
            return page;
        }finally {
            if(fetchResult != null){
                fetchResult.discardContent();
            }
        }

    }

    /**
     *
     */
    public void shutDown(){
        try {
            lock.lock();
            if (connectionMonitorThread != null) {
                connectionManager.shutdown();
                connectionMonitorThread.shutdown();
            }
        }finally {
            lock.unlock();
        }
    }

}

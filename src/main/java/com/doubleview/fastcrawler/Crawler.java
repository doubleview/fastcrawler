package com.doubleview.fastcrawler;


import com.doubleview.fastcrawler.dispatcher.PageDispatcher;
import com.doubleview.fastcrawler.dispatcher.SimpleDispatcher;
import com.doubleview.fastcrawler.exceptions.ContentFetchException;
import com.doubleview.fastcrawler.exceptions.NotAllowBinaryDataException;
import com.doubleview.fastcrawler.exceptions.PageParserException;
import com.doubleview.fastcrawler.exceptions.PageSizeOverException;
import com.doubleview.fastcrawler.fetcher.FetchResult;
import com.doubleview.fastcrawler.fetcher.PageFetcher;
import com.doubleview.fastcrawler.handler.*;
import com.doubleview.fastcrawler.parser.BinaryData;
import com.doubleview.fastcrawler.parser.PageParser;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * The crawler with multi threads  manages a crawler session
 *
 * @author doubleview
 */
public class Crawler extends CrawlerConfiguable {

    protected static final Logger logger = LoggerFactory.getLogger(Crawler.class);

    private final AtomicLong handledPageCount = new AtomicLong(0);

    protected CrawlerConfig crawlerConfig;

    protected PageFetcher pageFetcher;

    protected PageDispatcher pageDispatcher;

    protected PageHandler pageHandler;

    protected PageParser pageParser;

    protected List<ResultHandler> resultHandlers;

    protected int threadCount = 1;

    protected AtomicReference<Status> status = new AtomicReference<>(Status.NEW);

    protected ReentrantLock urlLock = new ReentrantLock();

    protected Condition urlCondition = urlLock.newCondition();

    private ExecutorService handlePool;

    private CountDownLatch exitOnComplete;

    private AtomicInteger waitUrlThreadCount = new AtomicInteger();

    private boolean isShutDown = false;

    private long startTime;

    /**
     * construct the Crawler object with default config
     * @param crawlerConfig
     */
    private Crawler(CrawlerConfig crawlerConfig) {
        super(crawlerConfig);
        this.crawlerConfig = crawlerConfig;
        setPageFetcher(new PageFetcher(crawlerConfig));
        setPageDispatcher(new SimpleDispatcher());
        setPageParser(new PageParser(crawlerConfig));
    }

    /**
     *
     * create a Crawler object with a  CrawlerConfig
     * @param crawlerConfig the default CrawlerConfig
     * @return the  object which manage the crawler
     */
    public static Crawler create(CrawlerConfig crawlerConfig) {
        return new Crawler(crawlerConfig);
    }

    /**
     *create a Crawler object with a  CrawlerConfig
     * @return the  object which manage the crawler
     */
    public static Crawler create() {
        return create(CrawlerConfig.custom());
    }


    /**
     * wait the crawler util the state is terminated
     */
    protected void waitUtilTerminated() {
        try {
            while (status.get() != Status.TERMINATED) {
                exitOnComplete.await();
            }
            shutDown();
        } catch (InterruptedException e) {
            logger.error("error occurred : {}", e);
        }
    }

    /**
     * start the crawler session
     * this method is a blocking session and it will wait util the crawler is terminated
     * @param pageHandler the object that implement the handle logic
     */
    public void start(PageHandler pageHandler) {
        if (exitOnComplete == null) {
            exitOnComplete = new CountDownLatch(threadCount);
        }
        startSync(pageHandler);
        waitUtilTerminated();
    }


    /**
     * start the crawler session
     * this method is a sync session and return the method immediately
     * @param pageHandler the object that implement the handle logic
     */
    public Crawler startSync(PageHandler pageHandler) {
        this.pageHandler = pageHandler;
        if (CollectionUtils.isEmpty(resultHandlers)) {
            addResultHandler(new ConsoleHandler());
        }
        ensureNotRunnalbe();
        if (!status.compareAndSet(Status.NEW, Status.RUNNABLE)) {
           logger.warn("crawler's state is not valid : {}" , status.get());
            return this;
        }
        int threadCount = getThreadCount();
        if (handlePool == null) {
            handlePool = Executors.newFixedThreadPool(threadCount);
        }
        logger.info("crawler started with {} threads" , threadCount);
        startTime = System.currentTimeMillis();
        for (int i = 0; i < threadCount; i++) {
            handlePool.execute(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        CrawlerRequest request = pageDispatcher.poll();
                        while (request == null) {
                            if (Status.TERMINATED == status.get()) {
                                if(exitOnComplete!= null) exitOnComplete.countDown();
                                shutDown();
                                return;
                            }
                            waitUrl();
                            request = pageDispatcher.poll();
                        }
                        try {
                            handleCrawlerRequest(request);
                        } catch (Exception e) {
                            logger.error("error occurred ", e);
                        } finally {
                            handledPageCount.incrementAndGet();
                            signalUrl();
                        }
                    }
                }
            });
        }
        return this;
    }



    /**
     * this method will handle the crawler request , fetch the page of the request
     * and process the result of the page by the resultHandler
     * @param request the request object of the current url
     */
    protected void handleCrawlerRequest(CrawlerRequest request) throws InterruptedException, PageSizeOverException, IOException, ContentFetchException, NotAllowBinaryDataException, PageParserException {
        FetchResult fetchResult = pageFetcher.fetchPage(request);
        if(fetchResult == null) return;
        Page page = pageFetcher.loadPage(fetchResult, request);
        if (page.isRedirect()) {
            if (crawlerConfig.isFollowRedirects() && pageHandler.shouldVisit(page, page.getRedirectRequest())) {
                addCrawlerRequest(page.getRedirectRequest());
            }
            return;
        }
        pageParser.parse(page);
        boolean notIgnore = pageHandler.handle(page);
        //the binary should's not be processed by resultHandler
        if(page.getType() == Page.TYPE.BINARY){
            return;
        }
        //add followRequests for crawler
        if (CollectionUtils.isNotEmpty(page.getFollowRequest())) {
            for (CrawlerRequest followRequest : page.getFollowRequest()) {
                if (pageHandler.shouldVisit(page, followRequest)) {
                    addCrawlerRequest(followRequest);
                }
            }
        }
        //handle the page result
        if (notIgnore && CollectionUtils.isNotEmpty(resultHandlers) && page.getCrawlerResult()!= null) {
            for (ResultHandler resultHandler : resultHandlers) {
                resultHandler.handle(page.getCrawlerResult(), request);
            }
        }
    }


    /**
     *the simple method which will get the binaryData from a custom url
     * @param downLoadUrl the url which will be downloaded
     * @param parentPath the patentPath of the download file
     */
    public static void downLoad(String downLoadUrl , String parentPath) {
        CrawlerConfig crawlerConfig = CrawlerConfig.custom().setBinaryStorePath(parentPath).
                setIncludeBinaryContent(true);
        Crawler.create(crawlerConfig).addRootUrl(downLoadUrl).start(new BinaryPageHandler());
    }

    /**
     * no request can be handled  and the current thread will wait util other thread
     *  create the request to the PageDispatcher
     */
    private void waitUrl() {
        urlLock.lock();
        try {
            waitUrlThreadCount.incrementAndGet();
            if (waitUrlThreadCount.get() == threadCount) {
                status.compareAndSet(Status.RUNNABLE, Status.TERMINATED);
                signalUrl();
                return;
            }
            urlCondition.await(10000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            logger.error("error occurred : {}", e);
        } finally {
            waitUrlThreadCount.decrementAndGet();
            urlLock.unlock();
        }
    }


    /**
     * current thread create some requests and it will notify
     *  other threads to handle request
     */
    private void signalUrl() {
        try {
            urlLock.lock();
            urlCondition.signalAll();
        } finally {
            urlLock.unlock();
        }
    }


    /**
     * check the state is not runnable state
     */
    protected void ensureNotRunnalbe() {
        if (status.get() == Status.RUNNABLE) {
            throw new IllegalStateException("crawler is already running!");
        }
    }


    /**
     * stop the crawler session
     */
    public synchronized void shutDown() {
        if(isShutDown()) return;
        setShutDown(true);
        status.set(Status.TERMINATED);
        handlePool.shutdown();
        pageFetcher.shutDown();
        logger.info("crawler shutDown");
        long endTime = System.currentTimeMillis();
        logger.info("crawler handled : {} pages" , handledPageCount.get());
        logger.info("crawler total time : {} seconds" , TimeUnit.MILLISECONDS.toSeconds(endTime - startTime));
    }


    /**
     * add a root url to the PageDispatcher
     * @param urls
     * @return
     */
    public Crawler addRootUrl(String... urls) {
        for (String url : urls) {
            try {
                CrawlerRequest request = new CrawlerRequest(url);
                request.setDepth(1);
                addCrawlerRequest(request);
            } catch (Exception e) {
                logger.error("error occurred while construct request url : {}" , url);
            }
        }
        return this;
    }

    /**
     * add a request object to the PageDispatcher
     * @param crawlerRequest
     */
    private void addCrawlerRequest(CrawlerRequest crawlerRequest) {
        pageDispatcher.push(crawlerRequest);
    }

    /**
     * add one more ResultHandler to process the result items
     * @param resultHandler
     */
    public Crawler addResultHandler(ResultHandler resultHandler) {
        ensureNotRunnalbe();
        if (this.resultHandlers == null) {
            this.resultHandlers = new ArrayList<>();
        }
        resultHandlers.add(resultHandler);
        return this;
    }

    public void addResultHandlers(List<ResultHandler> list) {
        ensureNotRunnalbe();
        if (this.resultHandlers == null) {
            this.resultHandlers = new ArrayList<>();
        }
        resultHandlers.addAll(list);
    }

    public int getThreadCount() {
        return threadCount;
    }

    public List<ResultHandler> getResultHandlers() {
        return resultHandlers;
    }

    public void setResultHandlers(List<ResultHandler> resultHandlers) {
        this.resultHandlers = resultHandlers;
    }

    public Crawler setThreadCount(int threadCount) {
        ensureNotRunnalbe();
        if(threadCount <= 0){
            throw new IllegalArgumentException("threadCount must a positive number");
        }
        this.threadCount = threadCount;
        return this;
    }

    public CrawlerConfig getCrawlerConfig() {
        return crawlerConfig;
    }

    public void setCrawlerConfig(CrawlerConfig crawlerConfig) {
        this.crawlerConfig = crawlerConfig;
    }

    public PageFetcher getPageFetcher() {
        return pageFetcher;
    }

    public void setPageFetcher(PageFetcher pageFetcher) {
        this.pageFetcher = pageFetcher;
    }

    public PageDispatcher getPageDispatcher() {
        return pageDispatcher;
    }

    public void setPageDispatcher(PageDispatcher pageDispatcher) {
        this.pageDispatcher = pageDispatcher;
    }

    public PageParser getPageParser() {
        return pageParser;
    }

    public void setPageParser(PageParser pageParser) {
        this.pageParser = pageParser;
    }

    public boolean isShutDown() {
        return isShutDown;
    }

    public void setShutDown(boolean shutDown) {
        isShutDown = shutDown;
    }

    private enum Status {
        NEW,
        RUNNABLE,
        TERMINATED
    }
}

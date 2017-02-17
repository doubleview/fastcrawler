package com.doubleview.fastcrawler;

/**
 *  configuration of the fastcrawler
 *
 *  @author doubleview
 *  @version 1.0
 *  
 */
public class CrawlerConfig {

    private int retryTimes = 0;

    private int cycleRetryTimes = 0;

    private int retrySleepTime = 1000;

    private int maxDepth= -1;

    private int maxPages = -1;

    private String userAgent = "fastcrawler (https://github.com/doubleview/fastcrawler)";

    private int maxConnsPerHost = 100;

    private int maxTotalConns = 100;

    private int socketTimeout = 20000;

    private int connectionTimeout = 30000;

    private int fetchTimeDelay = 200;

    private int maxDownloadSize = 1048576;

    private boolean followRedirects = true;

    private String proxyHost = null;

    private int proxyPort = 80;

    private String proxyUsername = null;

    private String proxyPassword = null;

    private boolean includeBinaryContent = false;
    
    public static CrawlerConfig custom(){
        return new CrawlerConfig();
    }
    
    public int getMaxDepth() {
        return maxDepth;
    }

    public CrawlerConfig setMaxDepth(int maxDepth) {
        this.maxDepth = maxDepth;
        return this;
    }

    public int getMaxPages() {
        return maxPages;
    }

    public CrawlerConfig setMaxPages(int maxPages) {
        this.maxPages = maxPages;
        return this;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public CrawlerConfig setUserAgent(String userAgent) {
        this.userAgent = userAgent;
        return this;
    }

    public int getMaxConnsPerHost() {
        return maxConnsPerHost;
    }

    public CrawlerConfig setMaxConnsPerHost(int maxConnsPerHost) {
        this.maxConnsPerHost = maxConnsPerHost;
        return this;
    }

    public int getMaxTotalConns() {
        return maxTotalConns;
    }

    public CrawlerConfig setMaxTotalConns(int maxTotalConns) {
        this.maxTotalConns = maxTotalConns;
        return this;
    }

    public int getSocketTimeout() {
        return socketTimeout;
    }

    public CrawlerConfig setSocketTimeout(int socketTimeout) {
        this.socketTimeout = socketTimeout;
        return this;
    }

    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    public CrawlerConfig setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
        return this;
    }


    public int getMaxDownloadSize() {
        return maxDownloadSize;
    }

    public CrawlerConfig setMaxDownloadSize(int maxDownloadSize) {
        this.maxDownloadSize = maxDownloadSize;
        return this;
    }

    public boolean isFollowRedirects() {
        return followRedirects;
    }

    public CrawlerConfig setFollowRedirects(boolean followRedirects) {
        this.followRedirects = followRedirects;
        return this;
    }


    public String getProxyHost() {
        return proxyHost;
    }

    public CrawlerConfig setProxyHost(String proxyHost) {
        this.proxyHost = proxyHost;
        return this;
    }

    public int getProxyPort() {
        return proxyPort;
    }

    public CrawlerConfig setProxyPort(int proxyPort) {
        this.proxyPort = proxyPort;
        return this;
    }

    public String getProxyUsername() {
        return proxyUsername;
    }

    public CrawlerConfig setProxyUsername(String proxyUsername) {
        this.proxyUsername = proxyUsername;
        return this;
    }

    public String getProxyPassword() {
        return proxyPassword;
    }

    public CrawlerConfig setProxyPassword(String proxyPassword) {
        this.proxyPassword = proxyPassword;
        return this;
    }

    public int getRetryTimes() {
        return retryTimes;
    }

    public void setRetryTimes(int retryTimes) {
        this.retryTimes = retryTimes;
    }

    public int getCycleRetryTimes() {
        return cycleRetryTimes;
    }

    public void setCycleRetryTimes(int cycleRetryTimes) {
        this.cycleRetryTimes = cycleRetryTimes;
    }

    public int getRetrySleepTime() {
        return retrySleepTime;
    }

    public void setRetrySleepTime(int retrySleepTime) {
        this.retrySleepTime = retrySleepTime;
    }

    public int getFetchTimeDelay() {
        return fetchTimeDelay;
    }

    public void setFetchTimeDelay(int fetchTimeDelay) {
        this.fetchTimeDelay = fetchTimeDelay;
    }

    public boolean isIncludeBinaryContent() {
        return includeBinaryContent;
    }

    public void setIncludeBinaryContent(boolean includeBinaryContent) {
        this.includeBinaryContent = includeBinaryContent;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CrawlerConfig that = (CrawlerConfig) o;

        if (maxDepth != that.maxDepth) return false;
        if (maxPages != that.maxPages) return false;
        if (maxConnsPerHost != that.maxConnsPerHost) return false;
        if (maxTotalConns != that.maxTotalConns) return false;
        if (socketTimeout != that.socketTimeout) return false;
        if (connectionTimeout != that.connectionTimeout) return false;
        if (maxDownloadSize != that.maxDownloadSize) return false;
        if (followRedirects != that.followRedirects) return false;
        if (proxyPort != that.proxyPort) return false;
        if (!userAgent.equals(that.userAgent)) return false;
        if (!proxyHost.equals(that.proxyHost)) return false;
        if (!proxyUsername.equals(that.proxyUsername)) return false;
        return proxyPassword.equals(that.proxyPassword);

    }

    @Override
    public int hashCode() {
        int result = maxDepth;
        result = 31 * result + maxPages;
        result = 31 * result + userAgent.hashCode();
        result = 31 * result + maxConnsPerHost;
        result = 31 * result + maxTotalConns;
        result = 31 * result + socketTimeout;
        result = 31 * result + connectionTimeout;
        result = 31 * result + maxDownloadSize;
        result = 31 * result + (followRedirects ? 1 : 0);
        result = 31 * result + proxyHost.hashCode();
        result = 31 * result + proxyPort;
        result = 31 * result + proxyUsername.hashCode();
        result = 31 * result + proxyPassword.hashCode();
        return result;
    }
}

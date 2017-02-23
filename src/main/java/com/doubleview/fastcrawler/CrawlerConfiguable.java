package com.doubleview.fastcrawler;

/**
 * every class of fastcrawler extend this class to make them configurable.
 * @author doubleview
 */
public abstract  class CrawlerConfiguable {

    protected  CrawlerConfig crawlerConfig;

    public CrawlerConfiguable(CrawlerConfig crawlerConfig) {
        setCrawlerConfig(crawlerConfig);
    }

    protected CrawlerConfig getCrawlerConfig() {
        return crawlerConfig;
    }

    protected void setCrawlerConfig(CrawlerConfig crawlerConfig) {
        this.crawlerConfig = crawlerConfig;
    }
}

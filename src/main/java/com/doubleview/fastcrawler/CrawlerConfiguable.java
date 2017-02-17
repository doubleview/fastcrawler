package com.doubleview.fastcrawler;

/**
 * CrawlerConiguable
 *
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

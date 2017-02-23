package com.doubleview.fastcrawler.dispatcher;

import com.doubleview.fastcrawler.CrawlerRequest;

/**
 * PageDispatcher object store the all the crawler request and manage the
 * request dispatcher
 * @author doubleview
 */
public interface PageDispatcher {

    /**
     * add a url
     * @param request
     */
     void push(CrawlerRequest request);

    /**
     * get an url to crawler
     * @return the url to crawl
     */
     CrawlerRequest poll();

    /**
     *
     * @return
     */
    int getLeftRequestsCount();

    /**
     *
     * @return
     */
    int getTotalRequestsCount();
}

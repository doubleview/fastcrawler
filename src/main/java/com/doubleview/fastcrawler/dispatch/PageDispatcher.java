package com.doubleview.fastcrawler.dispatch;

import com.doubleview.fastcrawler.CrawlerRequest;

/**
 * PageDispatcher is the part of url management.<br>
 * You can implement interface PageDispatcher to do:
 * manage urls to fetch
 * remove duplicate urls
 *
 * @author doubleview
 *
 */
public interface PageDispatcher {

    /**
     * add a url
     * @param request
     */
     void push(CrawlerRequest request);

    /**
     * get an url to crawl
     *
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

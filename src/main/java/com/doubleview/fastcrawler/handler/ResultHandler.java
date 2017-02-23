package com.doubleview.fastcrawler.handler;


import com.doubleview.fastcrawler.CrawlerRequest;
import com.doubleview.fastcrawler.fetcher.CrawlerResult;

/**
 * ResultHandler is a offline process the crawler and it will be implemented to some ways
 * @author doubleview
 */
public interface ResultHandler {

    void handle(CrawlerResult crawlerResult , CrawlerRequest request);
}

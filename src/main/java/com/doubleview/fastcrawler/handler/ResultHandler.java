package com.doubleview.fastcrawler.handler;


import com.doubleview.fastcrawler.CrawlerRequest;
import com.doubleview.fastcrawler.fetcher.CrawlerResult;

public interface ResultHandler {

    void handle(CrawlerResult crawlerResult , CrawlerRequest request);
}

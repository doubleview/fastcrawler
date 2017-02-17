package com.doubleview.fastcrawler.handler;

import com.doubleview.fastcrawler.CrawlerRequest;
import com.doubleview.fastcrawler.Page;

import java.io.IOException;

/**
 *
 */
public interface PageHandler {

    void handle(Page page) throws IOException;

    boolean shouldVisit(Page page , CrawlerRequest request);

}

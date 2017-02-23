package com.doubleview.fastcrawler.handler;

import com.doubleview.fastcrawler.CrawlerRequest;
import com.doubleview.fastcrawler.Page;

import java.io.IOException;

/**
 *  this interface will be implemented by user to customize the logic
 *  of the crawler
 *  @author doubleview
 */
public interface PageHandler {

    boolean handle(Page page) throws IOException;

    boolean shouldVisit(Page page , CrawlerRequest request);

}

package com.doubleview.fastcrawler.dispatcher;

import com.doubleview.fastcrawler.CrawlerRequest;

/**
 * Remove duplicate requests.
 * @author doubleview
 */
public interface DuplicateStrategy {

     boolean isSeenBefore(CrawlerRequest request);

     void resetCheck();

     int getSize();

}

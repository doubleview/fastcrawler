package com.doubleview.fastcrawler.dispatcher;

import com.doubleview.fastcrawler.CrawlerRequest;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author doubleview
 */
public class SimpleDuplicateStrategy implements DuplicateStrategy {

    private Set<String> urls = Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>());

    @Override
    public boolean isSeenBefore(CrawlerRequest request) {
        return !urls.add(getUrl(request));
    }

    protected String getUrl(CrawlerRequest request) {
        return request.getUrl();
    }

    @Override
    public void resetCheck() {
        urls.clear();
    }

    @Override
    public int getSize() {
        return urls.size();
    }
}

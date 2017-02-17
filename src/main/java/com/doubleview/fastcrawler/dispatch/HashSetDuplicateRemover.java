package com.doubleview.fastcrawler.dispatch;

import com.doubleview.fastcrawler.CrawlerRequest;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author doubleview
 */
public class HashSetDuplicateRemover implements DuplicateRemover {

    private Set<String> urls = Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>());

    @Override
    public boolean isDuplicate(CrawlerRequest request) {
        return !urls.add(getUrl(request));
    }

    protected String getUrl(CrawlerRequest request) {
        return request.getUrl();
    }

    @Override
    public void resetDuplicateCheck() {
        urls.clear();
    }

    @Override
    public int getTotalRequestsCount() {
        return urls.size();
    }
}

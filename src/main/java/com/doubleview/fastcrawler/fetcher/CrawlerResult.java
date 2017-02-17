package com.doubleview.fastcrawler.fetcher;


import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 */
public class CrawlerResult {

    private Map<String, Object> storeData = new LinkedHashMap<>();

    private boolean ignore;

    public <T> T  get(String key) {
        Object o = storeData.get(key);
        if (o == null) {
            return null;
        }
        return (T) storeData.get(key);
    }

    public Map<String, Object> getAllData() {
        return storeData;
    }

    public <T> CrawlerResult put(String key, T value) {
        storeData.put(key, value);
        return this;
    }


    public boolean isIgnore() {
        return ignore;
    }

    public CrawlerResult setIgnore(boolean ignore) {
        this.ignore = ignore;
        return this;
    }

    @Override
    public String toString() {
        return "CrawlerResult{" +
                "storeData=" + storeData +
                ", ignore=" + ignore +
                '}';
    }
}

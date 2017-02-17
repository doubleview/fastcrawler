package com.doubleview.fastcrawler.parser;


import org.apache.commons.collections.CollectionUtils;

import java.util.List;

public abstract class AbstractResponseData implements ResponseData {

    @Override
    public String get() {
        if (CollectionUtils.isNotEmpty(getAll())) {
            return getAll().get(0);
        }
        return null;
    }

    @Override
    public List<String> getAll() {
        return toStrings();
    }

    protected abstract List<String> toStrings();

    @Override
    public String toString() {
        return get();
    }
}

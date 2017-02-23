package com.doubleview.fastcrawler;

import java.util.HashMap;
import java.util.Map;

/**
 * the request object which represent the url request, the included url
 * is a http url
 *  @author doubleview
 */
public class CrawlerRequest {

    private String url;

    private String parentUrl;

    private int depth;

    private String domain;

    private String subDomain;

    private String path;

    private int priority;

    private Map<String , Object> configInfo;

    public static final String CYCLE_TRIED_TIMES = "cycle_tried_times";

    public CrawlerRequest(String url) {
        setURL(url);
    }

    public String getUrl() {
        return url;
    }

    public void setURL(String url) {
        this.url = url;
        int domainStartIdx = url.indexOf("//") + 2;
        int domainEndIdx = url.indexOf('/', domainStartIdx);
        domainEndIdx = (domainEndIdx > domainStartIdx) ? domainEndIdx : url.length();
        domain = url.substring(domainStartIdx, domainEndIdx);
        subDomain = "";
        String[] parts = domain.split("\\.");
        if (parts.length > 2) {
            domain = parts[parts.length - 2] + "." + parts[parts.length - 1];
            int limit = 2;
            for (int i = 0; i < (parts.length - limit); i++) {
                if (!subDomain.isEmpty()) {
                    subDomain += ".";
                }
                subDomain += parts[i];
            }
        }
        path = url.substring(domainEndIdx);
        int pathEndIdx = path.indexOf('?');
        if (pathEndIdx >= 0) {
            path = path.substring(0, pathEndIdx);
        }
    }

    public String getParentUrl() {
        return parentUrl;
    }

    public CrawlerRequest setParentUrl(String parentUrl) {
        this.parentUrl = parentUrl;
        return this;
    }

    public int getDepth() {
        return depth;
    }

    public CrawlerRequest setDepth(int depth) {
        this.depth = depth;
        return this;
    }

    public String getDomain() {
        return domain;
    }

    public CrawlerRequest setDomain(String domain) {
        this.domain = domain;
        return this;
    }

    public String getSubDomain() {
        return subDomain;
    }

    public CrawlerRequest setSubDomain(String subDomain) {
        this.subDomain = subDomain;
        return this;
    }

    public int getPriority() {
        return priority;
    }

    public CrawlerRequest setPriority(int priority) {
        this.priority = priority;
        return this;
    }

    public Object getConfigInfo(String key) {
        if (configInfo == null) {
            return null;
        }
        return configInfo.get(key);
    }

    public CrawlerRequest putConfigInfo(String key, Object value) {
        if (configInfo == null) {
            configInfo = new HashMap<>();
        }
        configInfo.put(key, value);
        return this;
    }

    @Override
    public String toString() {
        return "CrawlerRequest{" +
                "url='" + url + '\'' +
                ", parentUrl='" + parentUrl + '\'' +
                ", depth=" + depth +
                ", domain='" + domain + '\'' +
                ", subDomain='" + subDomain + '\'' +
                ", path='" + path + '\'' +
                ", priority=" + priority +
                '}';
    }
}

package com.doubleview.fastcrawler.fetcher;


import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author doubleview
 */
public class FetchResult {

    protected static final Logger logger = LoggerFactory.getLogger(FetchResult.class);

    protected int statusCode;

    protected HttpEntity entity;

    protected Header[] responseHeaders = null;

    protected  String fetchUrl = null;

    protected  String moveToUrl = null;

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public HttpEntity getEntity() {
        return entity;
    }

    public void setEntity(HttpEntity entity) {
        this.entity = entity;
    }

    public Header[] getResponseHeaders() {
        return responseHeaders;
    }

    public void setResponseHeaders(Header[] responseHeaders) {
        this.responseHeaders = responseHeaders;
    }

    public String getFetchUrl() {
        return fetchUrl;
    }

    public void setFetchUrl(String fetchUrl) {
        this.fetchUrl = fetchUrl;
    }

    public String getMoveToUrl() {
        return moveToUrl;
    }

    public void setMoveToUrl(String moveToUrl) {
        this.moveToUrl = moveToUrl;
    }

    public void discardContent(){
        try {
            if (entity != null) {
                EntityUtils.consume(entity);
            }
        }catch (Exception e){
            logger.warn("Unexpected error occurred while trying to discard content", e);
        }
    }
}

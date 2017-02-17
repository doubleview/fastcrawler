package com.doubleview.fastcrawler;

import com.doubleview.fastcrawler.fetcher.CrawlerResult;
import com.doubleview.fastcrawler.parser.BinaryData;
import com.doubleview.fastcrawler.parser.HtmlData;
import com.doubleview.fastcrawler.parser.JsonData;
import com.doubleview.fastcrawler.parser.PlainTextData;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;

/**
 *
 */
public class Page {

    protected CrawlerRequest crawlerRequest;

    protected byte[] contentData;

    protected String contentType;

    protected String contentEncoding;

    protected String contentCharset;

    protected String language;

    protected boolean truncated = false;

    protected CrawlerResult crawlerResult;

    protected  boolean redirect;

    protected  CrawlerRequest redirectRequest;

    protected List<CrawlerRequest> followRequest;

    protected TYPE type;

    protected BinaryData binaryData;

    protected HtmlData htmlData;

    protected JsonData jsonData;

    protected PlainTextData plainTextData;

    public enum TYPE{
        HTML,
        PLAINTEXT,
        JSON,
        BINARY
    }


    public Page() {
        super();
    }

    public Page(CrawlerRequest crawlerRequest) {
        this.crawlerRequest = crawlerRequest;
    }


    /**
     * Loads the content of this page from a fetched HttpEntity.
     *
     * @param entity HttpEntity
     * @param maxBytes The maximum number of bytes to read
     * @throws Exception when load fails
     */
    public void load(HttpEntity entity, int maxBytes) throws Exception {
        contentType = null;
        Header type = entity.getContentType();
        if (type != null) {
            contentType = type.getValue();
        }
        contentEncoding = null;
        Header encoding = entity.getContentEncoding();
        if (encoding != null) {
            contentEncoding = encoding.getValue();
        }

        Charset charset = ContentType.getOrDefault(entity).getCharset();
        if (charset != null) {
            contentCharset = charset.displayName();
        }

        contentData = toByteArray(entity, maxBytes);
    }

    /**
     * Read contents from an entity, with a specified maximum. This is a replacement of
     * EntityUtils.toByteArray because that function does not impose a maximum size.
     *
     * @param entity The entity from which to read
     * @param maxBytes The maximum number of bytes to read
     * @return A byte array containing maxBytes or fewer bytes read from the entity
     *
     * @throws IOException Thrown when reading fails for any reason
     */
    protected byte[] toByteArray(HttpEntity entity, int maxBytes) throws IOException {
        if (entity == null) {
            return new byte[0];
        }

        InputStream is = entity.getContent();
        int size = (int) entity.getContentLength();
        if (size <= 0 || size > maxBytes) {
            size = maxBytes;
        }

        int actualSize = 0;

        byte[] buf = new byte[size];
        while (actualSize < size) {
            int remain = size - actualSize;
            int readBytes = is.read(buf, actualSize, Math.min(remain, 1500));

            if (readBytes <= 0) {
                break;
            }

            actualSize += readBytes;
        }

        // Poll to see if there are more bytes to read. If there are,
        // the content has been truncated
        int ch = is.read();
        if (ch >= 0) {
            truncated = true;
        }

        // If the actual size matches the size of the buffer, do not copy it
        if (actualSize == buf.length) {
            return buf;
        }

        // Return the subset of the byte buffer that was used
        return Arrays.copyOfRange(buf, 0, actualSize);
    }


    public CrawlerRequest getCrawlerRequest() {
        return crawlerRequest;
    }

    public void setCrawlerRequest(CrawlerRequest crawlerRequest) {
        this.crawlerRequest = crawlerRequest;
    }

    public boolean isRedirect() {
        return redirect;
    }

    public void setRedirect(boolean redirect) {
        this.redirect = redirect;
    }

    public CrawlerRequest getRedirectRequest() {
        return redirectRequest;
    }

    public void setRedirectRequest(CrawlerRequest redirectRequest) {
        this.redirectRequest = redirectRequest;
    }

    public void setCrawlerResult(CrawlerResult crawlerResult) {
        this.crawlerResult = crawlerResult;
    }

    public byte[] getContentData() {
        return contentData;
    }

    public void setContentData(byte[] contentData) {
        this.contentData = contentData;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getContentEncoding() {
        return contentEncoding;
    }

    public void setContentEncoding(String contentEncoding) {
        this.contentEncoding = contentEncoding;
    }

    public String getContentCharset() {
        return contentCharset;
    }

    public void setContentCharset(String contentCharset) {
        this.contentCharset = contentCharset;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public List<CrawlerRequest> getFollowRequest() {
        return followRequest;
    }

    public void setFollowRequest(List<CrawlerRequest> followRequest) {
        this.followRequest = followRequest;
    }

    public boolean isTruncated() {
        return truncated;
    }

    public void setTruncated(boolean truncated) {
        this.truncated = truncated;
    }

    public CrawlerResult getCrawlerResult() {
        return crawlerResult;
    }

    public TYPE getType() {
        return type;
    }

    public void setType(TYPE type) {
        this.type = type;
    }

    public BinaryData getBinaryData() {
        return binaryData;
    }

    public void setBinaryData(BinaryData binaryData) {
        this.binaryData = binaryData;
    }

    public HtmlData getHtmlData() {
        return htmlData;
    }

    public void setHtmlData(HtmlData htmlData) {
        this.htmlData = htmlData;
    }

    public JsonData getJsonData() {
        return jsonData;
    }

    public void setJsonData(JsonData jsonData) {
        this.jsonData = jsonData;
    }

    public PlainTextData getPlainTextData() {
        return plainTextData;
    }

    public void setPlainTextData(PlainTextData plainTextData) {
        this.plainTextData = plainTextData;
    }

    public void addResult(String key , Object object) {
        if (crawlerResult == null) {
            crawlerResult = new CrawlerResult();
        }
        crawlerResult.put(key , object);
    }
}

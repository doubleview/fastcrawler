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
 * This class contains the data for a fetched and parsed page.
 *
 * @author doubleview
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

    protected List<CrawlerRequest> followBinaryRequest;

    protected TYPE type;

    protected BinaryData binaryData;

    protected HtmlData htmlData;

    protected JsonData jsonData;

    protected PlainTextData plainTextData;

    public enum TYPE{
        HTML,
        PLAINTEXT,
        JSON,
        BINARY,
    }


    public Page() {
        super();
    }

    public Page(CrawlerRequest crawlerRequest) {
        this.crawlerRequest = crawlerRequest;
    }


    /**
     * load the content of this page from a fetched HttpEntity.
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
        }else {
            contentCharset = Charset.defaultCharset().displayName();
        }

        contentData = toByteArray(entity, maxBytes);
    }

    /**
     * read contents from an entity, with a specified maximum.
     * @param entity The entity from which to read
     * @param maxBytes The maximum number of bytes to read
     * @return A byte array containing maxBytes or fewer bytes read from the entity
     *
     * @throws IOException thrown when reading fails for any reason
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

        int ch = is.read();
        if (ch >= 0) {
            truncated = true;
        }

        if (actualSize == buf.length) {
            return buf;
        }

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

    public List<CrawlerRequest> getFollowBinaryRequest() {
        return followBinaryRequest;
    }

    public void setFollowBinaryRequest(List<CrawlerRequest> followBinaryRequest) {
        this.followBinaryRequest = followBinaryRequest;
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

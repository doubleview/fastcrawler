package com.doubleview.fastcrawler.parser;


import com.doubleview.fastcrawler.CrawlerConfig;
import com.doubleview.fastcrawler.CrawlerConfiguable;
import com.doubleview.fastcrawler.CrawlerRequest;
import com.doubleview.fastcrawler.Page;
import com.doubleview.fastcrawler.exceptions.NotAllowBinaryDataException;
import com.doubleview.fastcrawler.exceptions.PageParserException;
import com.doubleview.fastcrawler.utils.NetUtils;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * this class will identify the content type of the page result
 * and parse the page
 *
 * @author doubleview
 */
public class PageParser extends CrawlerConfiguable {

    protected static final Logger logger = LoggerFactory.getLogger(PageParser.class);

    public PageParser(CrawlerConfig crawlerConfig) {
        super(crawlerConfig);
    }

    /**
     * parse the Page object according to the page data
     *
     * @param page
     * @throws NotAllowBinaryDataException
     * @throws PageParserException
     */
    public void parse(Page page) throws NotAllowBinaryDataException, PageParserException {
        if (hasBinaryContent(page.getContentType())) {
            if (!getCrawlerConfig().isIncludeBinaryContent()) {
                throw new NotAllowBinaryDataException("not allow binary data");
            }
            BinaryData binaryData = new BinaryData(page.getContentData());
            binaryData.setExtenstion(getBinaryExtension(page.getCrawlerRequest().getUrl()));
            String binaryStorePath = getCrawlerConfig().getBinaryStorePath();
            if (binaryStorePath != null && BinaryData.STORE_PATH == null) {
                BinaryData.STORE_PATH = binaryStorePath;
            }
            page.setType(Page.TYPE.BINARY);
            page.setBinaryData(binaryData);
        } else if (hasPlainTextContent(page.getContentType())) {
            String contentCharset = page.getContentCharset();
            try {
                String plainText = new String(page.getContentData(), contentCharset);
                PlainTextData plainTextData = new PlainTextData(plainText);
                page.setType(Page.TYPE.PLAINTEXT);
                page.setPlainTextData(plainTextData);
            } catch (UnsupportedEncodingException e) {
                logger.error("{}, while parsing: {}", e.getMessage(), page.getCrawlerRequest().getUrl());
                throw new PageParserException();
            }
        } else if (hasJsonTextContent(page.getContentType())) {
            String contentCharset = page.getContentCharset();
            try {
                String jsonText = new String(page.getContentData(), contentCharset);
                JsonData jsonData = new JsonData(jsonText);
                page.setType(Page.TYPE.JSON);
                page.setJsonData(jsonData);
            } catch (UnsupportedEncodingException e) {
                logger.error("{} , while parsing , {}", e.getMessage(), page.getCrawlerRequest().getUrl());
                throw new PageParserException();
            }
        } else {
            parseHtml(page);
        }
    }

    /**
     * the url include the html
     * this will parse the html data to the page object
     *
     * @param page
     * @throws PageParserException
     */
    private void parseHtml(Page page) throws PageParserException {
        try {
            String rawText = new String(page.getContentData(), page.getContentCharset());
            HtmlData htmlData = new HtmlData(NetUtils.canonicalizeHrefs(rawText, page.getCrawlerRequest().getUrl()));
            page.setHtmlData(htmlData);
            page.setType(Page.TYPE.HTML);
            //not bigger than maxdepth
            int maxDepth = getCrawlerConfig().getMaxDepth();
            if (maxDepth == -1 || maxDepth > page.getCrawlerRequest().getDepth()) {
                List<String> links = page.getHtmlData().getAllLinks(crawlerConfig.isIncludeBinaryContent());
                page.setFollowRequest(getFollowRequests(page , links));
            }
        } catch (UnsupportedEncodingException e) {
            logger.error("{}, while parsing: {}", e.getMessage(), page.getCrawlerRequest().getUrl());
            throw new PageParserException();
        }
    }

    /**
     *
     * @param page
     * @param urls
     * @return
     */
    private List<CrawlerRequest> getFollowRequests(Page page, List<String> urls) {
        List<CrawlerRequest> crawlerRequestList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(urls)) {
            for (String url : urls) {
                try {
                    CrawlerRequest request = new CrawlerRequest(NetUtils.canonicalizeURL(url,
                            page.getCrawlerRequest().getUrl()));
                    request.setParentUrl(page.getCrawlerRequest().getUrl());
                    request.setDepth(page.getCrawlerRequest().getDepth() + 1);
                    crawlerRequestList.add(request);
                } catch (Exception e) {
                    logger.error("error occurred while construct request url : {}", url);
                }
            }
        }
        return crawlerRequestList;
    }

    public static String getBinaryExtension(String url) {
        int start = url.lastIndexOf('.');
        int end = url.lastIndexOf('?');
        if (end == -1) {
            return url.substring(start);
        } else {
            return url.substring(start, end);
        }
    }

    public static boolean hasBinaryContent(String contentType) {
        String typeStr = (contentType != null) ? contentType.toLowerCase() : "";
        return typeStr.contains("image") || typeStr.contains("audio") ||
                typeStr.contains("video") || typeStr.contains("application") ||
                typeStr.contains("pdf");
    }

    public static boolean hasPlainTextContent(String contentType) {
        String typeStr = (contentType != null) ? contentType.toLowerCase() : "";
        return typeStr.contains("text") && !typeStr.contains("html");
    }

    public static boolean hasJsonTextContent(String contentType) {
        String typeStr = (contentType != null) ? contentType.toLowerCase() : "";
        return typeStr.contains("json");
    }

}

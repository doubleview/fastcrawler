package com.doubleview.fastcrawler.parser;


import com.doubleview.fastcrawler.CrawlerConfig;
import com.doubleview.fastcrawler.CrawlerConfiguable;
import com.doubleview.fastcrawler.CrawlerRequest;
import com.doubleview.fastcrawler.Page;
import com.doubleview.fastcrawler.exceptions.NotAllowBinaryDataException;
import com.doubleview.fastcrawler.exceptions.PageParserException;
import com.doubleview.fastcrawler.utils.UrlUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class PageParser  extends CrawlerConfiguable{

    protected static final Logger logger = LoggerFactory.getLogger(PageParser.class);

    public PageParser(CrawlerConfig crawlerConfig) {
        super(crawlerConfig);
    }

    public void parse(Page page) throws NotAllowBinaryDataException, PageParserException {
        if(hasBinaryContent(page.getContentType())){
            if (!getCrawlerConfig().isIncludeBinaryContent()) {
                throw new NotAllowBinaryDataException("not allow binary data");
            }
            BinaryData binaryData = new BinaryData(page.getContentData());
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
        }else if(hasJsonTextContent(page.getContentType())){
            String contentCharset = page.getContentCharset();
                try {
                    String jsonText = new String(page.getContentData(), contentCharset);
                    JsonData jsonData = new JsonData(jsonText);
                    page.setType(Page.TYPE.JSON);
                    page.setJsonData(jsonData);
                } catch (UnsupportedEncodingException e) {
                    logger.error("{} , while parsing , {}" , e.getMessage() , page.getCrawlerRequest().getUrl());
                    throw new PageParserException();
                }
        } else {
            try {
                String rawText = new String(page.getContentData() , page.getContentCharset());
                HtmlData htmlData = new HtmlData(UrlUtils.fixAllRelativeHrefs(rawText , page.getCrawlerRequest().getUrl()));
                page.setHtmlData(htmlData);
                page.setType(Page.TYPE.HTML);
                List<CrawlerRequest> crawlerRequestList = new ArrayList<>();
                //id not bigger than maxdepth
                int maxDepth = getCrawlerConfig().getMaxDepth();
                if(maxDepth == -1 || maxDepth >  page.getCrawlerRequest().getDepth()){
                    for (String url : page.getHtmlData().getAllLinks()) {
                        CrawlerRequest request = new CrawlerRequest(UrlUtils.canonicalizeUrl(url,
                                page.getCrawlerRequest().getUrl()));
                        request.setParentUrl(page.getCrawlerRequest().getUrl());
                        request.setDepth(page.getCrawlerRequest().getDepth() + 1);
                        crawlerRequestList.add(request);
                    }
                }
                page.setFollowRequest(crawlerRequestList);
            } catch (UnsupportedEncodingException e) {
                logger.error("{}, while parsing: {}", e.getMessage(), page.getCrawlerRequest().getUrl());
                throw new PageParserException();
            }
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

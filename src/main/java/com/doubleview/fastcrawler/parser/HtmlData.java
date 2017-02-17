package com.doubleview.fastcrawler.parser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Entities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 */
public class HtmlData extends AbstractResponseData {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private String htmlContent;

    private Document document;

    private List<Element> elements;

    public HtmlData(String htmlContent) {
        this.htmlContent = htmlContent;
        try {
            this.document = Jsoup.parse(htmlContent);
            this.elements = Collections.singletonList(document);
            disableJsoupHtmlEntityEscape();
        } catch (Exception e) {
            this.document = null;
            logger.warn("parse document error ", e);
        }
    }

    public HtmlData(List<Element> elements) {
        this.elements = elements;
    }

    private static volatile boolean INITED = false;

    public static boolean DISABLE_HTML_ENTITY_ESCAPE = false;

    private void disableJsoupHtmlEntityEscape() {
        if (DISABLE_HTML_ENTITY_ESCAPE && !INITED) {
            Entities.EscapeMode.base.getMap().clear();
            Entities.EscapeMode.extended.getMap().clear();
            Entities.EscapeMode.xhtml.getMap().clear();
            INITED = true;
        }
    }

    public List<String> getAllLinks() {
        PlainTextData plainTextData = (PlainTextData) css("a", "href");
        return plainTextData.toStrings();
    }

    public ResponseData css(String cssString) {
        return css(cssString, null);
    }

    public ResponseData css(String cssString, String attrName) {
        if (attrName == null) {
            List<Element> resultElements = new ArrayList<>();
            resultElements.addAll(this.document.select(cssString));
            return new HtmlData(resultElements);
        } else {
            List<String> stringList = new ArrayList<>();
            List<Element> elements = this.document.select(cssString);
            stringList.addAll(elements.stream().map(e -> e.attr(attrName)).collect(Collectors.toList()));
            return new PlainTextData(stringList);
        }
    }

    @Override
    protected List<String> toStrings() {
        List<String> resultStrings = new ArrayList<>();
        for (Element element : getElements()) {
            resultStrings.add(element.text());
        }
        return resultStrings;
    }

    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;
    }

    public String getHtmlContent() {
        return htmlContent;
    }

    public void setHtmlContent(String htmlContent) {
        this.htmlContent = htmlContent;
    }

    public List<Element> getElements() {
        return elements;
    }

    public void setElements(List<Element> elements) {
        this.elements = elements;
    }

}

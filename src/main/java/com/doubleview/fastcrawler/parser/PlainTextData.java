package com.doubleview.fastcrawler.parser;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * this class will represent the plain text data
 * @author doubleview
 */
public class PlainTextData extends AbstractResponseData{

    private String textContent;

    private List<String> textStrings = new ArrayList<>();

    public PlainTextData(String textContent) {
        this.textContent = textContent;
        this.textStrings = Collections.singletonList(textContent);
    }

    public PlainTextData(PlainTextData plainTextData) {
        this.textStrings.addAll(plainTextData.toStrings());
    }
    public PlainTextData(List<String> textStrings) {
        this.textStrings = textStrings;
    }

    public String getTextContent() {
        return textContent;
    }

    public void setTextContent(String textContent) {
        this.textContent = textContent;
    }

    public PlainTextData add(PlainTextData plainTextData) {
        this.textStrings.addAll(plainTextData.toStrings());
        return this;
    }

    @Override
    protected List<String> toStrings() {
        return textStrings;
    }
}

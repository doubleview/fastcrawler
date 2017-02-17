package com.doubleview.fastcrawler.parser;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PlainTextData extends AbstractResponseData{

    private String textContent;

    private List<String> textStrings = new ArrayList<>();

    public PlainTextData(String textContent) {
        this.textContent = textContent;
        this.textStrings = Collections.singletonList(textContent);
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


    @Override
    protected List<String> toStrings() {
        return textStrings;
    }
}

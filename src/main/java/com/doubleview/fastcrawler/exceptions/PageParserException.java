package com.doubleview.fastcrawler.exceptions;


public class PageParserException extends Exception{


    public PageParserException() {
        super();
    }

    public PageParserException(Throwable e) {
        super(e);
    }

    public PageParserException(String message) {
        super(message);
    }

    public PageParserException(String message , Throwable cause) {
        super(message , cause);
    }
}

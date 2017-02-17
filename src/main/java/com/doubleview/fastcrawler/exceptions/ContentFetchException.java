package com.doubleview.fastcrawler.exceptions;


public class ContentFetchException extends Exception{


    public ContentFetchException() {
        super();
    }

    public ContentFetchException(Throwable e) {
        super(e);
    }

    public ContentFetchException(String message) {
        super(message);
    }

    public ContentFetchException(String message , Throwable cause) {
        super(message , cause);
    }
}

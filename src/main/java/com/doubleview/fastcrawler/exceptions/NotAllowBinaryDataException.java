package com.doubleview.fastcrawler.exceptions;


/**
 * @author doubleview
 */
public class NotAllowBinaryDataException extends Exception{

    public NotAllowBinaryDataException() {
        super();
    }

    public NotAllowBinaryDataException(Throwable e) {
        super(e);
    }

    public NotAllowBinaryDataException(String message) {
        super(message);
    }

    public NotAllowBinaryDataException(String message , Throwable cause) {
        super(message , cause);
    }

}

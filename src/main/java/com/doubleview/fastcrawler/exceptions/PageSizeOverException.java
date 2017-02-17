package com.doubleview.fastcrawler.exceptions;


/**
 * @author  doubleview
 */
public class PageSizeOverException extends  Exception {

    long pageSize;

    public PageSizeOverException(long pageSize) {
            super("Aborted fetching of this URL as it's size ( " + pageSize +
                    " ) exceeds the maximum size");
            this.pageSize = pageSize;
    }


    public long getPageSize() {
        return pageSize;
    }
}

package com.doubleview.fastcrawler.parser;


import java.util.List;

/**
 *
 * @author doubleview
 */
public interface ResponseData {

    /**
     * this will return single result
     * @return
     */
    String get() ;

    /**
     *this will return all the results
     * @return
     */
    List<String> getAll();

    /**
     * this will return single result
     * @return
     */
    String toString();

}

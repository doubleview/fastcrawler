package com.doubleview.fastcrawler.handler;

import com.doubleview.fastcrawler.CrawlerRequest;
import com.doubleview.fastcrawler.Page;
import com.doubleview.fastcrawler.parser.BinaryData;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 *
 * @author  doubleview
 */
public class BinaryPageHandler extends AbstractPageHandler{

    /**
     *
     * @param page
     * @return
     */
    public boolean handleBinary(Page page) {
        BinaryData binaryData = page.getBinaryData();
        if(!filter(page)){
            return true;
        }
        String fileName = UUID.randomUUID().toString().replace("-", "") + binaryData.getExtenstion();
        try {
            binaryData.store(new File(BinaryData.STORE_PATH , fileName));
            return true;
        } catch (IOException e) {
            logger.error("error occurred while handling binary data  : " , e);
            return false;
        }
    }


    /**
     *
     * @param page
     * @return
     */
    public boolean filter(Page page) {
        return true;
    }

    @Override
    public boolean shouldVisit(Page page, CrawlerRequest request) {
        return true;
    }
}

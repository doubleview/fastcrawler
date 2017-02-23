package com.doubleview.fastcrawler.handler;

import com.doubleview.fastcrawler.CrawlerRequest;
import com.doubleview.fastcrawler.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;


/**
 *
 * @author doubleview
 */
public abstract class AbstractPageHandler implements PageHandler {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public boolean handle(Page page) throws IOException {
        if(page.getType() == Page.TYPE.HTML){
            return handleHtml(page);
        } else if (page.getType() == Page.TYPE.PLAINTEXT) {
            return handleText(page);
        }else if(page.getType() == Page.TYPE.JSON){
            return handleJson(page);
        } else if (page.getType() == Page.TYPE.BINARY) {
            return handleBinary(page);
        }
        return false;
    }


    public boolean handleHtml(Page page) {
        //handle in the subClass
        return  true;
    }


    public boolean handleBinary(Page page) {
        //handle in the subClass
        return true;
    }

    public boolean handleJson(Page page) {
        return true;
        //handle in the subClass
    }


    public boolean handleText(Page page) {
        return true;
        //handle in the subclass
    }

    @Override
    public abstract boolean shouldVisit(Page page, CrawlerRequest request);

}

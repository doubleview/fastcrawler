package com.doubleview.fastcrawler.example;


import com.doubleview.fastcrawler.Crawler;
import com.doubleview.fastcrawler.CrawlerConfig;
import com.doubleview.fastcrawler.CrawlerRequest;
import com.doubleview.fastcrawler.Page;
import com.doubleview.fastcrawler.handler.BinaryPageHandler;

/**
 *
 *
 * @author  doubleview
 */
public class MulDownloadImgs extends BinaryPageHandler{

    public static void main(String[] args) {
        CrawlerConfig config = CrawlerConfig.custom().setIncludeBinaryContent(true).setBinaryStorePath("H://fastcrawler");
        Crawler crawler = Crawler.create(config);
        crawler.setThreadCount(5).addRootUrl("http://www.7160.com/").start(new MulDownloadImgs());
    }

    @Override
    public boolean filter(Page page) {
        return page.getBinaryData().getExtenstion().matches("\\.jpg|\\.png|\\.gif");
    }

    @Override
    public boolean shouldVisit(Page page, CrawlerRequest request) {
        return true;
    }

}

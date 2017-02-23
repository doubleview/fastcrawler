package com.doubleview.fastcrawler.example;

import com.doubleview.fastcrawler.Crawler;
import com.doubleview.fastcrawler.CrawlerConfig;
import com.doubleview.fastcrawler.CrawlerRequest;
import com.doubleview.fastcrawler.Page;
import com.doubleview.fastcrawler.handler.AbstractPageHandler;

/**
 * @author doubleview
 */
public class JianShuHandler extends AbstractPageHandler {

    public static void main(String[] args) {
        CrawlerConfig crawlerConfig = CrawlerConfig.custom().setMaxDepth(10);
        Crawler.create(crawlerConfig).
                setThreadCount(5).
                //addResultHandler(new FileResultHandler("H://fastcrawler")).
                addRootUrl("http://www.jianshu.com/").
                startSync(new JianShuHandler());
    }

    /**
     *
     */
    public boolean handleHtml(Page page) {
        String passage = page.getHtmlData().css(".note .article .show-content").get();
        String title = page.getHtmlData().css(".note .article .title").get();
        if (passage != null) {
            page.addResult("title", title);
            page.addResult("passage", passage);
            return true;
        }
        return false;
    }

    @Override
    public boolean shouldVisit(Page page, CrawlerRequest request) {
        return request.getUrl().matches("http://www\\.jianshu\\.com/p/.+");
    }
}

package com.doubleview.fastcrawler.example;

import com.doubleview.fastcrawler.Crawler;
import com.doubleview.fastcrawler.CrawlerConfig;
import com.doubleview.fastcrawler.CrawlerRequest;
import com.doubleview.fastcrawler.Page;
import com.doubleview.fastcrawler.handler.PageHandler;

import java.io.File;
import java.io.IOException;

/**
 *
 *
 */
public class DoubanHandler implements PageHandler {

    public static void main(String[] args) {
        CrawlerConfig crawlerConfig = CrawlerConfig.custom();
        crawlerConfig.setIncludeBinaryContent(true);
        Crawler.create(crawlerConfig).addRootUrl("https://img1.doubanio.com/lpic/s29236389.jpg").start(new DoubanHandler());
    }

    @Override
    public void handle(Page page) throws IOException {
        //page.addResult("title" , page.getHtmlData().css(".popular-books .info .title a").getAll());

        page.getBinaryData().store(new File("H://a.jpg"));
    }

    @Override
    public boolean shouldVisit(Page page, CrawlerRequest request) {
        //System.out.println(request);
        return false;
    }
}

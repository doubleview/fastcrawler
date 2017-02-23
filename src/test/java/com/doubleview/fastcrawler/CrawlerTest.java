package com.doubleview.fastcrawler;

import com.doubleview.fastcrawler.handler.PageHandler;
import org.junit.Test;

import java.io.IOException;

public class CrawlerTest {

    @Test
    public void testRunnable() {
        Crawler crawler = Crawler.create().addRootUrl("http://github.com");
        crawler.startSync(new PageHandler() {
            @Override
            public boolean handle(Page page) throws IOException {
                return false;
            }

            @Override
            public boolean shouldVisit(Page page, CrawlerRequest request) {
                return false;
            }
        });
        crawler.setThreadCount(3);
    }

    /**
     *
     */
    @Test
    public void testBlock() {
        Crawler crawler = Crawler.create().addRootUrl("http://github.com");
        crawler.start(new PageHandler() {
            @Override
            public boolean handle(Page page) throws IOException {
                return false;
            }

            @Override
            public boolean shouldVisit(Page page, CrawlerRequest request) {
                return false;
            }
        });
        System.out.println("end");
    }
}

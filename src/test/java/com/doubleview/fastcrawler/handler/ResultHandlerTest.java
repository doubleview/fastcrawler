package com.doubleview.fastcrawler.handler;


import com.doubleview.fastcrawler.CrawlerRequest;
import com.doubleview.fastcrawler.fetcher.CrawlerResult;
import org.junit.Test;

public class ResultHandlerTest {

    @Test
    public void testConsoleHandler() {
        ResultHandler handler = new ConsoleHandler();
        CrawlerResult result = new CrawlerResult();
        result.put("title", "I am a title");
        result.put("content", "I am a content");
        handler.handle(result , new CrawlerRequest("http://www.baidu.com"));
    }


    @Test
    public void testFileHandler() {
        ResultHandler handler = new FileResultHandler("H://fastcrawler");
        CrawlerResult result = new CrawlerResult();
        result.put("title", "I am a title");
        result.put("content", "I am a content");
        handler.handle(result , new CrawlerRequest("http://www.baidu.com"));
    }
}

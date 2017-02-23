package com.doubleview.fastcrawler.parser;


import com.doubleview.fastcrawler.CrawlerConfig;
import com.doubleview.fastcrawler.CrawlerRequest;
import com.doubleview.fastcrawler.Page;
import com.doubleview.fastcrawler.fetcher.FetchResult;
import com.doubleview.fastcrawler.fetcher.PageFetcher;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class PageParserTest {


    Page page;

    @BeforeClass
    public void parse() throws  Exception{
        PageFetcher pageFetcher = new PageFetcher(CrawlerConfig.custom());
        CrawlerRequest request = new CrawlerRequest("http://baidu.com");
        FetchResult fetchResult = pageFetcher.fetchPage(request);
        page = pageFetcher.loadPage(fetchResult , request);

        PageParser pageParser = new PageParser(CrawlerConfig.custom());
        pageParser.parse(page);
    }

    @Test
    public void testPageParser() {
        Assert.assertEquals(Page.TYPE.HTML , page.getType());
        Assert.assertNotNull(page.getHtmlData());
        Assert.assertNull(page.getBinaryData());
        Assert.assertNull(page.getJsonData());
        Assert.assertNull(page.getPlainTextData());
    }

}

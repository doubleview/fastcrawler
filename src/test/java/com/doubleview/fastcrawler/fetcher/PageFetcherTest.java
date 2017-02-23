package com.doubleview.fastcrawler.fetcher;


import com.doubleview.fastcrawler.CrawlerConfig;
import com.doubleview.fastcrawler.CrawlerRequest;
import com.doubleview.fastcrawler.Page;
import com.doubleview.fastcrawler.exceptions.ContentFetchException;
import com.doubleview.fastcrawler.exceptions.PageSizeOverException;
import org.junit.Assert;
import org.junit.Test;
import java.io.IOException;

public class PageFetcherTest {

    @Test
    public void testFetcher() throws InterruptedException, PageSizeOverException, IOException {
        PageFetcher pageFetcher = new PageFetcher(CrawlerConfig.custom());
        FetchResult fetchResult = pageFetcher.fetchPage(new CrawlerRequest("http://github.com"));
        Assert.assertEquals("http://github.com" , fetchResult.getFetchUrl());
        //Assert.assertEquals(fetchResult.getStatusCode() , "200");
        Assert.assertEquals( 301 , fetchResult.getStatusCode());
        Assert.assertNotNull(fetchResult.getMoveToUrl());
        Assert.assertNotNull(fetchResult.getEntity());
    }

    @Test
    public void testPage() throws InterruptedException, PageSizeOverException, IOException, ContentFetchException {
        PageFetcher pageFetcher = new PageFetcher(CrawlerConfig.custom());
        CrawlerRequest request = new CrawlerRequest("http://baiducom");
        FetchResult fetchResult = pageFetcher.fetchPage(request);
        Page page = pageFetcher.loadPage(fetchResult , request);

        Assert.assertNull(page.getRedirectRequest());
        Assert.assertFalse(page.isRedirect());
    }
}

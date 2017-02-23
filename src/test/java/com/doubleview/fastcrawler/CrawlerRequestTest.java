package com.doubleview.fastcrawler;


import org.junit.Assert;
import org.junit.Test;

public class CrawlerRequestTest {


    @Test
    public void testRquest() {
        CrawlerRequest request = new CrawlerRequest("http://www.github.com/doubleview");
        Assert.assertEquals("github.com" , request.getDomain());
        Assert.assertEquals("www" , request.getSubDomain());
        Assert.assertEquals("http://www.github.com/doubleview" , request.getUrl());
    }

}

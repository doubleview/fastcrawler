package com.doubleview.fastcrawler.parser;

import org.junit.Assert;
import org.junit.Test;

public class HtmlDataTest {

    @Test
    public void testCss() {
        HtmlData htmlData = new HtmlData("<div><p width=\"500px\"><a href=\"http://www.github.com\">I am a href</a></p></div>");
        String link = htmlData.getAllLinks().get(0);
        Assert.assertEquals("http://www.github.com", link);

        String hrefText = htmlData.cssText("div p a").get();
        Assert.assertEquals(hrefText , "I am a href");

        String href = htmlData.css("div p a").get();
        Assert.assertEquals("<a href=\"http://www.github.com\">I am a href</a>" , href);

        String attribute = htmlData.css("div p", "width").get();
        Assert.assertEquals("500px" , attribute);
    }
}

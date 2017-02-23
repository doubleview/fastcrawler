package com.doubleview.fastcrawler;

import com.doubleview.fastcrawler.utils.NetUtils;
import org.junit.Assert;
import org.junit.Test;

public class NetUtilsTest {

    @Test
    public void testCanonicalize() {
        String url = NetUtils.canonicalizeURL("test", "http://www.github.com/x123");
        Assert.assertEquals(url , "http://www.github.com/test");
        url =  NetUtils.canonicalizeURL("../abc", "http://www.github.com/x123/sss");
        Assert.assertEquals(url , "http://www.github.com/abc");
        url = NetUtils.canonicalizeURL("../../abc", "http://www.github.com/x123/sss/bbb");
        Assert.assertEquals(url , "http://www.github.com/abc");
    }


    @Test
    public void testCanonicalizeHrefs() {
        String originHtml = "<p>innerhtml<p><a href=\"/start\"><p>innerhtml<p>";
        String replacedHtml = NetUtils.canonicalizeHrefs(originHtml, "http://www.github.com/");
        Assert.assertEquals(replacedHtml , "<p>innerhtml<p><a href=\"http://www.github.com/start\"><p>innerhtml<p>");

        originHtml = "<p>innerhtml<p><a href=\"/test a\"><p>innerhtml<p>";
        replacedHtml = NetUtils.canonicalizeHrefs(originHtml, "http://www.github.com/");
        Assert.assertEquals(replacedHtml , "<p>innerhtml<p><a href=\"http://www.github.com/test%20a\"><p>innerhtml<p>");

    }

}

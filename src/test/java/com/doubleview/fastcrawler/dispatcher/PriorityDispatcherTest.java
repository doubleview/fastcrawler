package com.doubleview.fastcrawler.dispatcher;

import com.doubleview.fastcrawler.CrawlerRequest;
import org.junit.Assert;
import org.junit.Test;

public class PriorityDispatcherTest {

    @Test
    public void testPriority() {
        PageDispatcher dispatcher = new PriorityDispatcher();
        CrawlerRequest c1 = new CrawlerRequest("http://www.baidu.com");
        c1.setPriority(1);
        CrawlerRequest c2 = new CrawlerRequest("http://www.github.com");
        CrawlerRequest c3 = new CrawlerRequest("http://www.jingdong.com");
        c3.setPriority(-1);
        dispatcher.push(c1);
        dispatcher.push(c2);
        dispatcher.push(c3);

        Assert.assertEquals(c1 , dispatcher.poll());
        Assert.assertEquals(c2 , dispatcher.poll());
        Assert.assertEquals(c3 , dispatcher.poll());
    }
}

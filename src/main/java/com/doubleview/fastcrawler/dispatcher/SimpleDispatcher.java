package com.doubleview.fastcrawler.dispatcher;

import com.doubleview.fastcrawler.CrawlerRequest;
import org.apache.http.annotation.ThreadSafe;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;


/**
 * simple implementation the PageDispatcher which uses the LinkedBlockingQueue
 * @author doubleview
 *
 */
@ThreadSafe
public class SimpleDispatcher extends AbstractPageDispather {

    private BlockingQueue<CrawlerRequest> queue = new LinkedBlockingQueue<>();

    @Override
    public void dispatch(CrawlerRequest request) {
        queue.add(request);
    }

    @Override
    public synchronized CrawlerRequest poll() {
        return queue.poll();
    }

    @Override
    public int getLeftRequestsCount() {
        return queue.size();
    }

    @Override
    public int getTotalRequestsCount() {
        return getDuplicateStrategy().getSize();
    }
}

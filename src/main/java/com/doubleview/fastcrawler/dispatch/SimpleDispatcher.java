package com.doubleview.fastcrawler.dispatch;

import com.doubleview.fastcrawler.CrawlerRequest;
import org.apache.http.annotation.ThreadSafe;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;


/**
 * Basic PageDispatcher implementation.<br>
 * Store urls to fetch in LinkedBlockingQueue and remove duplicate urls by HashMap.
 *
 * @author doubleview
 * @since 0.1.0
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
        return getDuplicateRemover().getTotalRequestsCount();
    }
}

package com.doubleview.fastcrawler.dispatcher;

import com.doubleview.fastcrawler.CrawlerRequest;
import org.apache.http.annotation.ThreadSafe;

import java.util.Comparator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * Priority dispatcher
 * @author doubleview
 */
@ThreadSafe
public class PriorityDispatcher extends AbstractPageDispather {

    public static final int INITIAL_CAPACITY = 5;

    private BlockingQueue<CrawlerRequest> noPriorityQueue = new LinkedBlockingQueue<>();

    private PriorityBlockingQueue<CrawlerRequest> priorityQueuePlus = new PriorityBlockingQueue<>(INITIAL_CAPACITY, new Comparator<CrawlerRequest>() {
        @Override
        public int compare(CrawlerRequest o1, CrawlerRequest o2) {
            return -Long.valueOf(o1.getPriority()).compareTo(Long.valueOf(o2.getPriority()));
        }
    });

    private PriorityBlockingQueue<CrawlerRequest> priorityQueueMinus = new PriorityBlockingQueue<>(INITIAL_CAPACITY, new Comparator<CrawlerRequest>() {
        @Override
        public int compare(CrawlerRequest o1, CrawlerRequest o2) {
            return -Long.valueOf(o1.getPriority()).compareTo(Long.valueOf(o2.getPriority()));
        }
    });

    @Override
    public void dispatch(CrawlerRequest request) {
        if (request.getPriority() == 0) {
            noPriorityQueue.add(request);
        } else if (request.getPriority() > 0) {
            priorityQueuePlus.put(request);
        } else {
            priorityQueueMinus.put(request);
        }
    }

    @Override
    public synchronized CrawlerRequest poll() {
        CrawlerRequest poll = priorityQueuePlus.poll();
        if (poll != null) {
            return poll;
        }
        poll = noPriorityQueue.poll();
        if (poll != null) {
            return poll;
        }
        return priorityQueueMinus.poll();
    }

    @Override
    public int getLeftRequestsCount() {
        return noPriorityQueue.size();
    }

    @Override
    public int getTotalRequestsCount() {
        return getDuplicateStrategy().getSize();
    }
}

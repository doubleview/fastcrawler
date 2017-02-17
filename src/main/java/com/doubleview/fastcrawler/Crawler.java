package com.doubleview.fastcrawler;


import com.doubleview.fastcrawler.dispatch.PageDispatcher;
import com.doubleview.fastcrawler.dispatch.SimpleDispatcher;
import com.doubleview.fastcrawler.exceptions.ContentFetchException;
import com.doubleview.fastcrawler.exceptions.NotAllowBinaryDataException;
import com.doubleview.fastcrawler.exceptions.PageParserException;
import com.doubleview.fastcrawler.exceptions.PageSizeOverException;
import com.doubleview.fastcrawler.fetcher.FetchResult;
import com.doubleview.fastcrawler.fetcher.PageFetcher;
import com.doubleview.fastcrawler.handler.ConsoleHandler;
import com.doubleview.fastcrawler.handler.PageHandler;
import com.doubleview.fastcrawler.handler.ResultHandler;
import com.doubleview.fastcrawler.parser.PageParser;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 *
 */
public class Crawler extends CrawlerConfiguable {


    protected static final Logger logger = LoggerFactory.getLogger(Crawler.class);

    private final AtomicLong handledPageCount = new AtomicLong(0);

    protected CrawlerConfig crawlerConfig;

    protected PageFetcher pageFetcher;

    protected PageDispatcher pageDispatcher;

    protected PageHandler pageHandler;

    protected PageParser pageParser;

    protected List<ResultHandler> resultHandlers;

    protected int threadCount = 1;

    protected AtomicReference<Status> status = new AtomicReference<>(Status.NEW);

    protected ReentrantLock urlLock = new ReentrantLock();

    protected Condition urlCondition = urlLock.newCondition();

    private ExecutorService handlePool;

    private CountDownLatch exitOnComplete;

    private AtomicInteger waitUrlThreadCount = new AtomicInteger();



    private Crawler(CrawlerConfig crawlerConfig) {
        super(crawlerConfig);
        this.crawlerConfig = crawlerConfig;
        setPageFetcher(new PageFetcher(crawlerConfig));
        setPageDispatcher(new SimpleDispatcher());
        setPageParser(new PageParser(crawlerConfig));
    }


    /**
     *
     * @param crawlerConfig
     * @return
     */
    public static Crawler create(CrawlerConfig crawlerConfig) {
        return new Crawler(crawlerConfig);
    }

    /**
     *
     * @return
     */
    public static Crawler create() {
        return create(CrawlerConfig.custom());
    }


    /**
     * wait the crawler util the state is terminated
     */
    protected void waitUtilTerminated() {
        try {
            while (status.get() != Status.TERMINATED) {
                exitOnComplete.await();
            }
            shutDown();
            logger.info("crawler is terminated");
        } catch (InterruptedException e) {
            logger.error("error occurred : {}", e);
        }
    }

    /**
     * @param pageHandler
     */
    public void start(PageHandler pageHandler) {
        startSync(pageHandler);
        if (exitOnComplete == null) {
            exitOnComplete = new CountDownLatch(threadCount);
        }
        waitUtilTerminated();
    }


    /**
     *
     * @param pageHandler
     */
    public void startSync(PageHandler pageHandler) {
        this.pageHandler = pageHandler;
        if (CollectionUtils.isEmpty(resultHandlers)) {
            addResultHandler(new ConsoleHandler());
        }
        ensureNotRunnalbe();
        if (!status.compareAndSet(Status.NEW, Status.RUNNABLE)) {
           logger.warn("crawler's state is not valid : {}" + status.get());
            return;
        }
        int threadCount = getThreadCount();
        if (handlePool == null) {
            handlePool = Executors.newFixedThreadPool(threadCount);
        }
        logger.info("crawler started with {} threads" , threadCount);
        for (int i = 0; i < threadCount; i++) {
            handlePool.execute(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        CrawlerRequest request = pageDispatcher.poll();
                        while (request == null) {
                            waitUrl();
                            if (status.get() == Status.TERMINATED) {
                                exitOnComplete.countDown();
                                return;
                            }
                            request = pageDispatcher.poll();
                        }
                        try {
                            handleCrawlerRequest(request);
                        } catch (Exception e) {
                            logger.error("error occurred : {}", e);
                        } finally {
                            handledPageCount.incrementAndGet();
                            signalUrl();
                        }
                        if (status.get() == Status.TERMINATED) {
                            exitOnComplete.countDown();
                            return;
                        }
                    }
                }
            });
        }
    }


    /**
     *
     * @param request
     */
    protected void handleCrawlerRequest(CrawlerRequest request) throws InterruptedException, PageSizeOverException, IOException, ContentFetchException, NotAllowBinaryDataException, PageParserException {
        FetchResult fetchResult = pageFetcher.fetchPage(request);
        if(fetchResult == null) return;
        Page page = pageFetcher.loadPage(fetchResult, request);
        if (page.isRedirect()) {
            if (crawlerConfig.isFollowRedirects() && pageHandler.shouldVisit(page, page.getRedirectRequest())) {
                addCrawlerRequest(page.getRedirectRequest());
            }
        }
        pageParser.parse(page);
        pageHandler.handle(page);
        if(page.getType() == Page.TYPE.BINARY){
            return;
        }
        if (CollectionUtils.isNotEmpty(resultHandlers) && page.getCrawlerResult()!= null) {
            for (ResultHandler resultHandler : resultHandlers) {
                resultHandler.handle(page.getCrawlerResult(), request);
            }
        }
        for (CrawlerRequest followRequest : page.getFollowRequest()) {
            if (pageHandler.shouldVisit(page, followRequest)) {
                addCrawlerRequest(followRequest);
            }
        }
    }

    /**
     *
     */
    private void waitUrl() {
        urlLock.lock();
        try {
            waitUrlThreadCount.incrementAndGet();
            if (waitUrlThreadCount.get() == threadCount) {
                status.compareAndSet(Status.RUNNABLE, Status.TERMINATED);
                signalUrl();
                return;
            }
            urlCondition.await(10000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            logger.error("Error occurred : {}", e);
        } finally {
            waitUrlThreadCount.decrementAndGet();
            urlLock.unlock();
        }
    }


    /**
     *
     */
    private void signalUrl() {
        try {
            urlLock.lock();
            urlCondition.signalAll();
        } finally {
            urlLock.unlock();
        }
    }


    protected void ensureNotRunnalbe() {
        if (status.get() == Status.RUNNABLE) {
            throw new IllegalStateException("Crawler is already running!");
        }
    }


    public void shutDown() {
        logger.info("crawler shutDown");
        pageFetcher.shutDown();
        handlePool.shutdown();
    }

    public Crawler addRootUrl(String... urls) {
        for (String url : urls) {
            addCrawlerRequest(new CrawlerRequest(url));
        }
        return this;
    }

    private void addCrawlerRequest(CrawlerRequest crawlerRequest) {
        pageDispatcher.push(crawlerRequest);
    }

    public void addResultHandler(ResultHandler resultHandler) {
        ensureNotRunnalbe();
        if (this.resultHandlers == null) {
            this.resultHandlers = new ArrayList<>();
        }
        resultHandlers.add(resultHandler);
    }

    public void addResultHandlers(List<ResultHandler> list) {
        ensureNotRunnalbe();
        if (this.resultHandlers == null) {
            this.resultHandlers = new ArrayList<>();
        }
        resultHandlers.addAll(list);
    }

    public int getThreadCount() {
        return threadCount;
    }

    public List<ResultHandler> getResultHandlers() {
        return resultHandlers;
    }

    public void setResultHandlers(List<ResultHandler> resultHandlers) {
        this.resultHandlers = resultHandlers;
    }

    public Crawler setThreadCount(int threadCount) {
        ensureNotRunnalbe();
        if(threadCount <= 0){
            throw new IllegalArgumentException("threadCount must a positive number");
        }
        this.threadCount = threadCount;
        return this;
    }

    public CrawlerConfig getCrawlerConfig() {
        return crawlerConfig;
    }

    public void setCrawlerConfig(CrawlerConfig crawlerConfig) {
        this.crawlerConfig = crawlerConfig;
    }

    public PageFetcher getPageFetcher() {
        return pageFetcher;
    }

    public void setPageFetcher(PageFetcher pageFetcher) {
        this.pageFetcher = pageFetcher;
    }

    public PageDispatcher getPageDispatcher() {
        return pageDispatcher;
    }

    public void setPageDispatcher(PageDispatcher pageDispatcher) {
        this.pageDispatcher = pageDispatcher;
    }

    public PageParser getPageParser() {
        return pageParser;
    }

    public void setPageParser(PageParser pageParser) {
        this.pageParser = pageParser;
    }

    private enum Status {
        NEW,
        RUNNABLE,
        TERMINATED
    }
}

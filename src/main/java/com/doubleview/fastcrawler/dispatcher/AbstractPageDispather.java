package com.doubleview.fastcrawler.dispatcher;

import com.doubleview.fastcrawler.CrawlerRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Remove duplicate urls and only push urls which are not duplicate
 *
 * @author doubleview
 */
public abstract class AbstractPageDispather implements PageDispatcher {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    private DuplicateStrategy duplicateStrategy = new SimpleDuplicateStrategy();

    public DuplicateStrategy getDuplicateStrategy() {
        return duplicateStrategy;
    }

    public AbstractPageDispather setDuplicateStrategy(DuplicateStrategy duplicateStrategy) {
        this.duplicateStrategy = duplicateStrategy;
        return this;
    }

    /**
     * push a request object to the dispatcher
     * @param request
     */
    @Override
    public void push(CrawlerRequest request) {
        logger.info("get a candidate url {}", request.getUrl());
        if (!duplicateStrategy.isSeenBefore(request) || shouldReserved(request)) {
            logger.debug("push to queue {}", request.getUrl());
            dispatch(request);
        }
    }

    /**
     *
     * @param request
     * @return
     */
    protected boolean shouldReserved(CrawlerRequest request) {
        return request.getConfigInfo(CrawlerRequest.CYCLE_TRIED_TIMES) != null;
    }

    /**
     *
     */
    protected void resetCheck() {
        getDuplicateStrategy().resetCheck();
    }

    abstract void dispatch(CrawlerRequest request);

}

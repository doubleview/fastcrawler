package com.doubleview.fastcrawler.dispatch;

import com.doubleview.fastcrawler.CrawlerRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Remove duplicate urls and only push urls which are not duplicate.<br><br>
 *
 * @author doubleview
 */
public abstract class AbstractPageDispather implements PageDispatcher {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    private DuplicateRemover duplicatedRemover = new HashSetDuplicateRemover();

    public DuplicateRemover getDuplicateRemover() {
        return duplicatedRemover;
    }

    public AbstractPageDispather setDuplicateRemover(DuplicateRemover duplicatedRemover) {
        this.duplicatedRemover = duplicatedRemover;
        return this;
    }

    @Override
    public void push(CrawlerRequest request) {
        logger.trace("get a candidate url {}", request.getUrl());
        if (!duplicatedRemover.isDuplicate(request) || shouldReserved(request)) {
            logger.debug("push to queue {}", request.getUrl());
            dispatch(request);
        }
    }

    protected boolean shouldReserved(CrawlerRequest request) {
        return request.getConfigInfo(CrawlerRequest.CYCLE_TRIED_TIMES) != null;
    }

    abstract void dispatch(CrawlerRequest request);

}

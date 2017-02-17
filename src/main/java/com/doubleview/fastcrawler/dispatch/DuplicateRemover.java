package com.doubleview.fastcrawler.dispatch;

import com.doubleview.fastcrawler.CrawlerRequest;

/**
 * Remove duplicate requests.
 * @author doubleview
 */
public interface DuplicateRemover {

    /**
     *
     * Check whether the request is duplicate.
     *
     * @param request request
     * @return true if is duplicate
     */
    public boolean isDuplicate(CrawlerRequest request);

    /**
     * Reset duplicate check.
     */
    public void resetDuplicateCheck();

    /**
     * Get TotalRequestsCount for monitor.
     * @return number of total request
     */
    public int getTotalRequestsCount();

}

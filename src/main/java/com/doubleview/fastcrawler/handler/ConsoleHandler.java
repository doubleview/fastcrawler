package com.doubleview.fastcrawler.handler;

import com.doubleview.fastcrawler.CrawlerRequest;
import com.doubleview.fastcrawler.fetcher.CrawlerResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;


public class ConsoleHandler implements ResultHandler {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void handle(CrawlerResult crawlerResult , CrawlerRequest request) {
        logger.debug("get crawler result from : {}"  , request.getUrl());
        Map<String , Object> data = crawlerResult.getAllData();
        for (String key : data.keySet()) {
            System.out.println(key + ": \t" + data.get(key));
        }
    }

}

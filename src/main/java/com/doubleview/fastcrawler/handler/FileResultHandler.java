package com.doubleview.fastcrawler.handler;


import com.doubleview.fastcrawler.CrawlerRequest;
import com.doubleview.fastcrawler.fetcher.CrawlerResult;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Map;

public class FileResultHandler implements ResultHandler{

    private Logger logger = LoggerFactory.getLogger(getClass());

    private String path;

    public FileResultHandler(String path) {
        this.path =path;
        if (!path.endsWith(File.pathSeparator)) {
            path += File.pathSeparator;
        }
    }

    private File getFile(String fileName) {
        int index = fileName.lastIndexOf(File.pathSeparator);
        if (index > 0) {
            String path = fileName.substring(0, index);
            File file = new File(path);
            if (!file.exists()) {
                file.mkdirs();
            }
        }
        return new File(fileName);
    }

    @Override
    public void handle(CrawlerResult crawlerResult, CrawlerRequest request) {
            try {
                PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(
                        new FileOutputStream(getFile(path +
                                DigestUtils.md5Hex(request.getUrl()) + ".html")),"UTF-8"));
                Map<String , Object> data = crawlerResult.getAllData();
                for (String key : data.keySet()) {
                    printWriter.println(key + ": \t" + data.get(key));
                }
            } catch (IOException e) {
                logger.error("Error occurred : {}", e);
            }
    }
}

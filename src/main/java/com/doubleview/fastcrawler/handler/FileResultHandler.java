package com.doubleview.fastcrawler.handler;


import com.doubleview.fastcrawler.CrawlerRequest;
import com.doubleview.fastcrawler.fetcher.CrawlerResult;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.FileAlreadyExistsException;
import java.util.Map;

/**
 * the result will stored to file
 * @author doubleview
 */
public class FileResultHandler implements ResultHandler{

    private Logger logger = LoggerFactory.getLogger(getClass());

    private String path;

    public FileResultHandler(String path) {
        this.path =path;
        if (!this.path.endsWith(File.separator)) {
            this.path += File.separator;
        }
    }

    private File getFile(String fileName) {
        int index = fileName.lastIndexOf(File.separator);
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
        PrintWriter pw = null;
            try {
                 pw = new PrintWriter(new OutputStreamWriter(
                        new FileOutputStream(getFile(path +
                                DigestUtils.md5Hex(request.getUrl()) + ".html")),"UTF-8"));
                Map<String , Object> data = crawlerResult.getAllData();
                for (String key : data.keySet()) {
                    pw.println(key + ": \t" + data.get(key));
                }
                pw.flush();
            } catch (IOException e) {
                logger.error("error occurred : {}", e);
            }finally {
                pw.close();
            }
    }
}

package com.doubleview.fastcrawler.example;

import com.doubleview.fastcrawler.Crawler;


/**
 *
 */
public class DownloadImg {

    public static void main(String[] args) {
        Crawler.downLoad(
                "http://edu-image.nosdn.127.net/8E12914771C3A24DEB20C8049DEDBA73.png?imageView&thumbnail=225y142&quality=100" ,
                "H://fastcrawler");
    }
}

package com.doubleview.fastcrawler.utils;

import org.apache.commons.lang3.StringUtils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * common net utils
 * @author doubleview
 */
public class NetUtils {


    /**
     * this method will get a canonicalized url
     * @param url the url which will be fixed
     * @param refer referenced url
     * @return the canonicalized url
     */
    public static String canonicalizeURL(String url, String refer) {
        URL base;
        try {
            base = new URL(refer);
            if (url.startsWith("?"))
                url = base.getPath() + url;
            URL abs = new URL(base, url);
            return abs.toExternalForm().replace(" ", "20%");
        } catch (MalformedURLException e) {
            return null;
        }
    }


    /**
     * this method can fix relative all the href of the html text
     * @param html html text which will be fixed
     * @param url the reference url
     * @return fixed html text
     */
    public static String canonicalizeHrefs(String html, String url) {
        html = replaceByPattern(html, url, Pattern.compile("(<a[^<>]*href=)[\"']([^\"'<>]*)[\"']", Pattern.CASE_INSENSITIVE));
        html = replaceByPattern(html, url, Pattern.compile("(<a[^<>]*href=)([^\"'<>\\s]+)", Pattern.CASE_INSENSITIVE));
        return html;
    }

    public static String replaceByPattern(String html, String url, Pattern pattern) {
        StringBuilder stringBuilder = new StringBuilder();
        Matcher matcher = pattern.matcher(html);
        int lastEnd = 0;
        boolean modified = false;
        while (matcher.find()) {
            modified = true;
            stringBuilder.append(StringUtils.substring(html, lastEnd, matcher.start()));
            stringBuilder.append(matcher.group(1));
            stringBuilder.append("\"").append(canonicalizeURL(matcher.group(2), url)).append("\"");
            lastEnd = matcher.end();
        }
        if (!modified) {
            return html;
        }
        stringBuilder.append(StringUtils.substring(html, lastEnd));
        return stringBuilder.toString();
    }
}

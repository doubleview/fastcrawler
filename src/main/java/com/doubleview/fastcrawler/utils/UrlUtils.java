package com.doubleview.fastcrawler.utils;

import org.apache.commons.lang3.StringUtils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**'
 *
 */
public class UrlUtils {


    public static String canonicalizeUrl(String url, String refer) {
        URL base;
        try {
            try {
                base = new URL(refer);
            } catch (MalformedURLException e) {
                // the base is unsuitable, but the attribute may be abs on its own, so try that
                URL abs = new URL(refer);
                return abs.toExternalForm();
            }
            // workaround: java resolves '//path/file + ?foo' to '//path/?foo', not '//path/file?foo' as desired
            if (url.startsWith("?"))
                url = base.getPath() + url;
            URL abs = new URL(base, url);
            return encodeIllegalCharacterInUrl(abs.toExternalForm());
        } catch (MalformedURLException e) {
            return "";
        }
    }

    public static String encodeIllegalCharacterInUrl(String url) {
        //TODO more charator support
        return url.replace(" ", "%20");
    }


    /**
     * allow blank space in quote
     */
    private static Pattern patternForHrefWithQuote = Pattern.compile("(<a[^<>]*href=)[\"']([^\"'<>]*)[\"']", Pattern.CASE_INSENSITIVE);

    /**
     * disallow blank space without quote
     */
    private static Pattern patternForHrefWithoutQuote = Pattern.compile("(<a[^<>]*href=)([^\"'<>\\s]+)", Pattern.CASE_INSENSITIVE);

    public static String fixAllRelativeHrefs(String html, String url) {
        html = replaceByPattern(html, url, patternForHrefWithQuote);
        html = replaceByPattern(html, url, patternForHrefWithoutQuote);
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
            stringBuilder.append("\"").append(canonicalizeUrl(matcher.group(2), url)).append("\"");
            lastEnd = matcher.end();
        }
        if (!modified) {
            return html;
        }
        stringBuilder.append(StringUtils.substring(html, lastEnd));
        return stringBuilder.toString();
    }
}

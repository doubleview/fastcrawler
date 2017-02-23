package com.doubleview.fastcrawler.parser;


import java.io.*;
import java.util.List;

/**
 * this will represent the binary data
 * @author doubleview
 */
public class BinaryData extends AbstractResponseData {

    protected  byte[] binaryContent;

    protected  String extenstion;

    public static String STORE_PATH;

    public BinaryData(byte[] binaryContent) {
        this.binaryContent = binaryContent;
    }

    public byte[] getBinaryContent() {
        return binaryContent;
    }

    public void setBinaryContent(byte[] binaryContent) {
        this.binaryContent = binaryContent;
    }

    public String getExtenstion() {
        return extenstion;
    }

    public void setExtenstion(String extenstion) {
        this.extenstion = extenstion;
    }


    public InputStream getInputStream() {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(binaryContent);
        return byteArrayInputStream;
    }

    public long getLength() {
        return binaryContent.length;
    }


    public void store(OutputStream out) throws IOException {
        out.write(binaryContent);
    }


    public void store(File file) throws IOException {
        store(new FileOutputStream(file));
    }

    @Override
    protected List<String> toStrings() {
        return null;
    }
}

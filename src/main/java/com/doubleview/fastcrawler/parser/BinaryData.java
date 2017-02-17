package com.doubleview.fastcrawler.parser;


import java.io.*;
import java.util.List;

public class BinaryData extends AbstractResponseData {

    protected  byte[] binaryContent;

    public BinaryData(byte[] binaryContent) {
        this.binaryContent = binaryContent;
    }

    public byte[] getBinaryContent() {
        return binaryContent;
    }

    public void setBinaryContent(byte[] binaryContent) {
        this.binaryContent = binaryContent;
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

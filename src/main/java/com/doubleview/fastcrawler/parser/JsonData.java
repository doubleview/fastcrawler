package com.doubleview.fastcrawler.parser;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * this object represent the json text data
 * @author doubleview
 */
public class JsonData extends PlainTextData{


    JSONObject jsonObject;

    public JsonData(String jsonText) {
        super(jsonText);
        this.jsonObject = JSON.parseObject(jsonText);
    }


    public JSONObject getJsonObject() {
        return jsonObject;
    }

    public void setJsonObject(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
    }
}

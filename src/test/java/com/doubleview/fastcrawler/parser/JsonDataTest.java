package com.doubleview.fastcrawler.parser;


import com.alibaba.fastjson.JSONObject;
import org.junit.Assert;
import org.junit.Test;

public class JsonDataTest {

    @Test
    public void testJsonParse() {
        JsonData jsonData = new JsonData("{\"age\":23,\"name\":\"hcc\", \"height\":172 ,\"weight\":\"62\"}");
        JSONObject jsonObject = jsonData.getJsonObject();

        int age = jsonObject.getInteger("age");
        Assert.assertEquals(23 , age);

        String name = jsonObject.getString("name");
        Assert.assertEquals("hcc" , name);

        int weight = jsonObject.getIntValue("weight");
        Assert.assertEquals(62 , weight);
    }
}

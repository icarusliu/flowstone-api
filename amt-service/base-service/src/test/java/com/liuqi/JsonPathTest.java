package com.liuqi;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * JSONPath测试
 *
 * @author  LiuQi 2024/9/21-21:24
 * @version V1.0
 **/
public class JsonPathTest {
    @Test
    public void test() {
        Map<String, Object> dataMap = new HashMap<>(16);
        Map<String, Object> map = new HashMap<>(16);
        map.put("data", dataMap);
        dataMap.put("test", 1);
        Integer i = JsonPath.read(map, "$.data.test");
        System.out.println(i);
    }
}

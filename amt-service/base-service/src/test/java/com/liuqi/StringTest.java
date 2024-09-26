package com.liuqi;

import org.junit.jupiter.api.Test;

import java.util.regex.Pattern;

/**
 * 
 *
 * @author  LiuQi 2024/9/24-11:35
 * @version V1.0
 **/
public class StringTest {
    @Test
    public void test() {
        String url = "jdbc:mysql://localhost:3306/acode_api?useUnicode=true&characterEncoding=utf8&useSSL=false&zeroDateTimeBehavior=convertToNull";
        url = url.replace("jdbc:mysql://", "");
        int idx = url.indexOf("/");
        url = url.substring(idx + 1);
        idx = url.indexOf("?");
        if (idx >= 0) {
            url = url.substring(0, idx);
        }
        System.out.println(url);
    }

    @Test
    public void test1() {
        String url = "jdbc:sqlserver://localhost:3306aaaaaa;databaseName=test;aabbeeee";
        Pattern pattern = Pattern.compile("(?<=databaseName=).*(?=;)");
        pattern.matcher(url).results().forEach(result -> {
            System.out.println(result.group());
        });
        System.out.println(pattern.matcher(url).group());;
    }
}

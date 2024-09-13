package com.liuqi.dua.executor.tasks.http;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * 请求上下文
 *
 * @author LiuQi 2024/9/13-16:59
 * @version V1.0
 **/
@Data
public class RequestContext {
    private Map<String, Object> params = new HashMap<>(16);
    private Map<String, Object> body = new HashMap<>(16);
    private Map<String, String> headers = new HashMap<>(16);
    private Map<String, String> cookie = new HashMap<>(16);

    public RequestContext addHeader(String key, String value) {
        headers.put(key, value);
        return this;
    }

    public RequestContext addCookie(String key, String value) {
        cookie.put(key, value);
        return this;
    }

    public RequestContext addBody(String key, Object value) {
        body.put(key, value);
        return this;
    }

    public RequestContext addParam(String key, Object value) {
        params.put(key, value);
        return this;
    }

    public void add(String type, String key, Object val) {
        if (null == val) {
            return;
        }

        switch (type) {
            case "cookie" -> this.addCookie(key, val.toString());
            case "header" -> this.addHeader(key, val.toString());
            case "body" -> this.addBody(key, val);
            case "query" -> this.addParam(key, val);
        }
    }
 }

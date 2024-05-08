package com.liuqi.aapi.bean.dto;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * API参数
 *
 * @author LiuQi 15:56
 **/
@Data
public class ApiParams {
    private Request request = new Request();

    private Map<String, Node> nodes = new HashMap<>();

    public void addNode(String code, Map<String, Object> params, Object output) {
        Node node = nodes.getOrDefault(code, new Node());
        node.setParams(params);
        node.setOutput(output);
    }

    @Data
    public static class Request {
        private Map<String, Object> params;

        private Object body;

        private Map<String, Object> pathParams;

        private Map<String, Object> headers;
    }

    @Data
    static class Node {
        private Map<String, Object> params;
        private Object output;
    }
}

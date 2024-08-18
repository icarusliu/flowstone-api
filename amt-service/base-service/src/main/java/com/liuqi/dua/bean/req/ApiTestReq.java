package com.liuqi.dua.bean.req;

import lombok.Data;

import java.util.Map;

/**
 * 接口测试请求参数
 *
 * @author  LiuQi 2024/8/10-23:50
 * @version V1.0
 **/
@Data
public class ApiTestReq {
    // 接口id
    private String id;

    private Map<String, Object> queryParams;

    private Object body;

    private Map<String, Object> headers;
}

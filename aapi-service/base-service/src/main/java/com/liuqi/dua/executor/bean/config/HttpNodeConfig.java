package com.liuqi.dua.executor.bean.config;

import lombok.Data;

/**
 * Http任务节点配置
 *
 * @author  LiuQi 2024/8/9-10:48
 * @version V1.0
 **/
@Data
public class HttpNodeConfig {
    private String supplier;
    private String path;
    private String method;

    // 超时时间
    private Long timeoutInSecond;

    // 认证规则
    private String auth;

    // 请求参数配置
    private String requestConfig;

    // 返回数据解析配置
    private String responseConfig;
}

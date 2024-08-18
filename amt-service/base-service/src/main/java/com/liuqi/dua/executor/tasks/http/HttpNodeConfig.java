package com.liuqi.dua.executor.tasks.http;

import com.liuqi.dua.executor.bean.NodeParam;
import lombok.Data;

import java.util.List;

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
    private List<NodeParam> params;

    // 路径参数
    private List<NodeParam> pathVariables;

    // 请求体
    private List<NodeParam> body;

    // 请求头
    private List<NodeParam> headers;

    // 是否启用批量处理
    private Boolean batch;
}

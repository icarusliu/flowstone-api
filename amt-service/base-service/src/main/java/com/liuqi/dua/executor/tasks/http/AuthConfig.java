package com.liuqi.dua.executor.tasks.http;

import lombok.Data;

import java.util.List;

/**
 * 认证配置
 *
 * @author  LiuQi 2024/9/13-14:40
 * @version V1.0
 **/
@Data
public class AuthConfig {
    private String name;
    private String type;

    private List<AuthParam> params;

    private String dynamicType;
    private String authUrl;
    private List<AuthParam> dynamicParams;
    private List<AuthParam> targetParams;
}

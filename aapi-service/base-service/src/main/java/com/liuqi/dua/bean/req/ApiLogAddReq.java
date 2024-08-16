package com.liuqi.dua.bean.req;

import lombok.Data;

/**
 * 接口执行日志新增对象
 *
 * @author Coder Generator 2024-08-14 14:42:30
 **/
@Data
public class ApiLogAddReq {
    private String apiId;
    private String apiPath;
    private String apiName;
    private int status;
    private String params;
    private String errorMsg;
    private String result;
}
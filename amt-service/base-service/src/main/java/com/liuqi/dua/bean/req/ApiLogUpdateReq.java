package com.liuqi.dua.bean.req;

import lombok.Data;

/**
 * 接口执行日志更新对象
 *
 * @author Coder Generator 2024-08-14 14:42:30
 **/
@Data
public class ApiLogUpdateReq {
    private String id;
    private String apiId;
    private String apiPath;
    private String apiName;
    private int status;
    private String params;
    private String errorMsg;
    private String result;
}
package com.liuqi.base.bean.req;

import lombok.Data;

/**
 * 客户端新增对象 
 * @author Coder Generator 2024-09-25 09:04:44 
 **/
@Data
public class ClientAddReq {
    private String name;
    private String remark;
    private String whiteIps;
    private Integer limitInSecond;
    private Boolean withAllApis;
}
package com.liuqi.base.bean.req;

import lombok.Data;

/**
 * 客户端更新对象 
 * @author Coder Generator 2024-09-25 09:04:44 
 **/
@Data
public class ClientUpdateReq {
    private String id;
    private String name;
    private String remark;
    private Boolean disabled;
    private String whiteIps;
    private Integer limitInSecond;
    private Boolean withAllApis;
}
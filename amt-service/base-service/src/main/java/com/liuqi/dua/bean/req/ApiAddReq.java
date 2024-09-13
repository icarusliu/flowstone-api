package com.liuqi.dua.bean.req;

import lombok.Data;

/**
 * 接口新增对象
 *
 * @author Coder Generator 2024-07-08 22:32:36
 **/
@Data
public class ApiAddReq {
    private String content;
    private String name;
    private String remark;
    private String path;
    private String method;
    private String typeId;
}
package com.liuqi.dua.bean.req;

import lombok.Data;

/**
 * 接口草稿更新对象
 *
 * @author Coder Generator 2024-07-08 22:33:46
 **/
@Data
public class ApiDraftUpdateReq {
    private String id;
    private String content;
    private String name;
    private String remark;
    private String path;
    private String typeId;
    private String method;
    private Integer status;

    private Boolean changeStatus = false;
}
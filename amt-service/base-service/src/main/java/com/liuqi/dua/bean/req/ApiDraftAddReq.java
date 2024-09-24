package com.liuqi.dua.bean.req;

import lombok.Data;

/**
 * 接口草稿新增对象
 *
 * @author Coder Generator 2024-07-08 22:33:46
 **/
@Data
public class ApiDraftAddReq {
    private Boolean guestMode;
    private String content;
    private String name;
    private String remark;
    private String path;
    private String method;
    private String typeId;
}
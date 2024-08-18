package com.liuqi.base.bean.req;

import lombok.Data;

@Data
public class SysConfigUpdateReq {
    private String id;
    private String code;
    private String name;
    private String value;
    private String remark;
    private Boolean enabled;
}
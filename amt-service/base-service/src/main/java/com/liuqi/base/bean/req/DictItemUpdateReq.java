package com.liuqi.base.bean.req;

import lombok.Data;

@Data
public class DictItemUpdateReq {
    private String id;

    private String code;

    private String name;

    private String value;

    private String remark;
}
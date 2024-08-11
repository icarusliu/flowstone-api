package com.liuqi.base.bean.req;

import lombok.Data;

@Data
public class DeptUpdateReq {
    private String id;

    private String code;

    private String name;

    private String parentId;

    private Integer sort;
}
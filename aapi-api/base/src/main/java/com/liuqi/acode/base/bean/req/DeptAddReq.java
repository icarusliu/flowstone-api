package com.liuqi.acode.base.bean.req;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class DeptAddReq {
    @NotBlank
    private String code;

    @NotBlank
    private String name;

    private String parentId = "-1";

    private Integer sort = 0;
}
package com.liuqi.acode.base.bean.req;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class SysConfigAddReq {
    @NotBlank
    private String code;

    @NotBlank
    private String name;

    @NotBlank
    private String value;

    private String remark;
}
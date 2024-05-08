package com.liuqi.acode.base.bean.req;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class DictItemAddReq {
    @NotBlank
    private String dictId;

    @NotBlank
    private String code;

    @NotBlank
    private String name;

    @NotBlank
    private String value;

    private String remark;
}
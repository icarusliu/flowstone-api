package com.liuqi.acode.base.bean.req;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class DictUpdateReq {
    @NotBlank
    private String id;

    private String code;

    private String name;

    private String remark;
}
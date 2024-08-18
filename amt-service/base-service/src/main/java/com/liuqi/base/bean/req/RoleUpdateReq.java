package com.liuqi.base.bean.req;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;

@Data
public class RoleUpdateReq {
    @NotBlank
    private String id;

    private String code;

    private String name;
}

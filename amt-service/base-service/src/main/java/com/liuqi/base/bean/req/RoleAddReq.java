package com.liuqi.base.bean.req;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;

@Data
public class RoleAddReq {
    @NotBlank
    private String code;

    @NotBlank
    private String name;
}

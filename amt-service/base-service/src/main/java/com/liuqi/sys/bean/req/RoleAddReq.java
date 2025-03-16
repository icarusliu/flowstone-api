package com.liuqi.sys.bean.req;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RoleAddReq {
    @NotBlank
    private String code;

    @NotBlank
    private String name;
}

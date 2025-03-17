package com.liuqi.sys.bean.req;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RoleUpdateReq {
    @NotBlank
    private String id;

    private String code;

    private String name;
}

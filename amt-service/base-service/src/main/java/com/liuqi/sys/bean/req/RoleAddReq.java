package com.liuqi.sys.bean.req;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RoleAddReq {
    private String appId;

    @NotBlank
    private String name;
}

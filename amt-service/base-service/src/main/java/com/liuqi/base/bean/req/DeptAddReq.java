package com.liuqi.base.bean.req;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DeptAddReq {
    @NotBlank
    private String code;

    @NotBlank
    private String name;

    private String parentId = "-1";

    private Integer sort = 0;
}
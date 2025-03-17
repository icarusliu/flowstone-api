package com.liuqi.sys.bean.req;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

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
package com.liuqi.base.bean.req;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DictAddReq {
    @NotBlank
    private String code;

    @NotBlank
    private String name;

    private String remark;
}
package com.liuqi.acode.base.bean.dto;

import com.liuqi.common.base.bean.dto.BaseDTO;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class DeptDTO extends BaseDTO {
    @NotBlank
    private String code;

    @NotBlank
    private String name;

    private String parentId;
    private Integer sort;
}
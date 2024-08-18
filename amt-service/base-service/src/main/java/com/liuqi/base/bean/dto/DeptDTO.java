package com.liuqi.base.bean.dto;

import com.liuqi.common.base.bean.dto.BaseDTO;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;


@Data
public class DeptDTO extends BaseDTO {
    @NotBlank
    private String code;

    @NotBlank
    private String name;

    private String parentId;
    private Integer sort;
}
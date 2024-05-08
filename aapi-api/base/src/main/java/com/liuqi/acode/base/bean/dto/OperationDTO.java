package com.liuqi.acode.base.bean.dto;

import com.liuqi.common.base.bean.dto.BaseDTO;
import lombok.Data;

@Data
public class OperationDTO extends BaseDTO {
    private String code;
    private String name;
    private String remark;
    private String menuId;
    private Integer sort;
    private Boolean enabled;
}
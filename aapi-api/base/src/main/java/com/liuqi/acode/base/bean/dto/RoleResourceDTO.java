package com.liuqi.acode.base.bean.dto;

import com.liuqi.common.base.bean.dto.BaseDTO;
import lombok.Data;

@Data
public class RoleResourceDTO extends BaseDTO {
    private String roleId;
    private Integer resourceType;
    private String resourceId;
}
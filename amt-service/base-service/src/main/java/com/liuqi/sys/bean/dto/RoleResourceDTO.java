package com.liuqi.sys.bean.dto;

import com.liuqi.common.base.bean.dto.BaseDTO;
import lombok.Data;

@Data
public class RoleResourceDTO extends BaseDTO {
    private String roleId;
    private String resourceType;
    private String resourceId;
}
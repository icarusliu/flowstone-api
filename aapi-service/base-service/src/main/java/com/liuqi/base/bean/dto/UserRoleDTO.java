package com.liuqi.base.bean.dto;

import com.liuqi.common.base.bean.dto.BaseDTO;
import lombok.Data;

@Data
public class UserRoleDTO extends BaseDTO {
    private String userId;
    private String roleId;
}
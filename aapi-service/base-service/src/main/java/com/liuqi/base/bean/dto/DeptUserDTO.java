package com.liuqi.base.bean.dto;

import com.liuqi.common.base.bean.dto.BaseDTO;
import lombok.Data;

@Data
public class DeptUserDTO extends BaseDTO {
    private String deptId;
    private String userId;
}
package com.liuqi.base.bean.dto;

import com.liuqi.common.base.bean.dto.BaseDTO;
import lombok.Data;

@Data
public class SysConfigDTO extends BaseDTO {
    private String code;
    private String name;
    private String value;
    private String remark;
    private Boolean enabled;
}
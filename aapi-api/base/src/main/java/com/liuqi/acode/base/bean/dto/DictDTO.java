package com.liuqi.acode.base.bean.dto;

import com.liuqi.common.base.bean.dto.BaseDTO;
import lombok.Data;

@Data
public class DictDTO extends BaseDTO {
    private String code;
    private String name;
    private String remark;
}
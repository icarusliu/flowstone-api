package com.liuqi.base.bean.dto;

import com.liuqi.common.base.bean.dto.BaseDTO;
import lombok.Data;

@Data
public class DictItemDTO extends BaseDTO {
    private String dictId;
    private String code;
    private String name;
    private String value;
    private String remark;
}
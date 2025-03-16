package com.liuqi.base.bean.dto;

import com.liuqi.base.bean.DictItem;
import com.liuqi.common.base.bean.dto.BaseDTO;
import lombok.Data;

import java.util.List;

@Data
public class DictDTO extends BaseDTO {
    private String code;
    private String name;
    private Integer status;

    private List<DictItem> items;
    private String remark;
}
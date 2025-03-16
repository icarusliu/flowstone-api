package com.liuqi.base.bean.dto;

import com.liuqi.base.bean.DictItem;
import com.liuqi.common.base.bean.dto.BaseDTO;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class DictDTO extends BaseDTO {
    /**
     * 字典类型，items：列表字段，sql: sql字典
     */
    private String type;
    private String code;
    private String name;
    private Integer status;

    private List<DictItem> items;
    private String remark;
    private Map<String, Object> metadata;
}
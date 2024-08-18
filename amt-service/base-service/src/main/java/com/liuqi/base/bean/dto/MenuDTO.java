package com.liuqi.base.bean.dto;

import com.liuqi.common.base.bean.dto.BaseDTO;
import lombok.Data;

@Data
public class MenuDTO extends BaseDTO {
    private String code;
    private String name;
    private String path;
    private MenuType type;
    private Boolean hide;
    private String parentId;
    private Integer sort;
}
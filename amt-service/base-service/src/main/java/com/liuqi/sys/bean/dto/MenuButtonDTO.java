package com.liuqi.sys.bean.dto;

import com.liuqi.common.base.bean.dto.BaseDTO;
import lombok.Data;

/**
 * 菜单按钮数据实体
 *
 * @author Coder Generator 2024-09-29 18:55:25
 **/
@Data
public class MenuButtonDTO extends BaseDTO {
    private String name;
    private String code;
    private String menuId;
    private Integer sort;
}
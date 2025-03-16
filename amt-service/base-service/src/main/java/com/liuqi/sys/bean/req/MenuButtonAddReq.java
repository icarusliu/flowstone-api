package com.liuqi.sys.bean.req;

import lombok.Data;

/**
 * 菜单按钮新增对象
 *
 * @author Coder Generator 2024-09-29 18:55:25
 **/
@Data
public class MenuButtonAddReq {
    private String name;
    private String code;
    private String menuId;
    private Integer sort;
}
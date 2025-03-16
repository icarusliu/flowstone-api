package com.liuqi.base.bean.req;

import lombok.Data;

/**
 * 菜单按钮更新对象
 *
 * @author Coder Generator 2024-09-29 18:55:25
 **/
@Data
public class MenuButtonUpdateReq {
    private String id;
    private String name;
    private String code;
    private String menuId;
    private Integer sort;
}
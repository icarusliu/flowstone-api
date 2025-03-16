package com.liuqi.sys.bean.dto;

import com.liuqi.common.base.bean.dto.TreeNode;
import lombok.Data;

import java.util.List;

@Data
public class MenuDTO extends TreeNode<MenuDTO> {
    private String code;
    private String icon;
    private String name;
    private String path;
    private MenuType type;
    private Boolean hide;
    private List<MenuButtonDTO> buttons;
}
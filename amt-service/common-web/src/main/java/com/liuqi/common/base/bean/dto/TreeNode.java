package com.liuqi.common.base.bean.dto;

import lombok.Data;

import java.util.List;

@Data
public class TreeNode<T> extends BaseDTO{
    private String parentId;
    private Integer sort;
    private List<T> children;
}

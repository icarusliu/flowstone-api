package com.liuqi.common.base.bean.dto;

import lombok.Data;

import java.util.List;

/**
 * 树形节点
 * @param <T>
 */
@Data
public class TreeNode<T> extends BaseDTO{
    private String parentId;
    private Integer sort;
    private List<T> children;
}

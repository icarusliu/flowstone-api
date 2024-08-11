package com.liuqi.common.base.bean.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Collection;

@Data
public class TreeNode extends BaseDTO{
    private String parentId;
    private Integer sort;
    private Collection<? extends TreeNode> children;
}

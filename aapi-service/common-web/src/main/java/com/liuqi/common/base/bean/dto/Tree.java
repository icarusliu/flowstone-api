package com.liuqi.common.base.bean.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Tree<T> extends TreeNode {
    private String id;

    private String label;

    private T data;
}

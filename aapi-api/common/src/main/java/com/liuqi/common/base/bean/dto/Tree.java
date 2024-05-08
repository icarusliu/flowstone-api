package com.liuqi.common.base.bean.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Collection;

@Data
@Builder
public class Tree<T> {
    private String id;

    private String label;

    private T data;

    private Collection<Tree<T>> children;
}

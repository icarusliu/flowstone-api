package com.liuqi.base.bean.req;

import lombok.Data;

/**
 * 模型分类更新对象 
 * @author Coder Generator 2025-03-14 12:16:28 
 **/
@Data
public class ModelTypeUpdateReq {
    private String id;
    /**
     * 模型分类名称
     */
    private String name;
    /**
     * 排序
     */
    private String sort;
    /**
     * 父级分类id
     */
    private String parentId;
}
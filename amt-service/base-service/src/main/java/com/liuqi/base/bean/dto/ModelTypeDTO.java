package com.liuqi.base.bean.dto;

import com.liuqi.common.base.bean.dto.TreeNode;
import lombok.Data;

/**
 * 模型分类数据实体 
 * @author Coder Generator 2025-03-14 12:16:28 
 **/
@Data
public class ModelTypeDTO extends TreeNode<ModelTypeDTO> {
    /**
     * 模型分类名称
     */
    private String name;
}
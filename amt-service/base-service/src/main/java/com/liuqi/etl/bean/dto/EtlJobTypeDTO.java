package com.liuqi.etl.bean.dto;

import com.liuqi.common.base.bean.dto.TreeNode;
import lombok.Data;

import java.util.List;

/**
 * 任务分类数据实体 
 * @author Coder Generator 2025-03-10 15:40:15 
 **/
@Data
public class EtlJobTypeDTO extends TreeNode<EtlJobTypeDTO>{
    /**
     * 分类编码
     */
    private String code;
    /**
     *分类名称
     **/
    private String name;
    /**
     *说明
     **/
    private String remark;
}
package com.liuqi.dua.bean.req;

import lombok.Data;

import java.util.Map;

/**
 * 模型字段新增对象 
 * @author Coder Generator 2025-03-14 12:59:51 
 **/
@Data
public class ModelFieldAddReq {
    /**
     * 所属模型
     */
    private String modelId;
    /**
     * 编码
     */
    private String code;
    /**
     * 字段名称
     */
    private String name;
    /**
     * 数据类型
     */
    private String dataType;
    /**
     * 是否可为空
     */
    private Boolean nullable;
    /**
     * 数据配置（如varchar后的长度等）
     */
    private String dataConfig;
    /**
     * 默认值
     */
    private String defaultValue;
    /**
     * 表单配置
     */
    private Map<String, Object> formConfig;
    /**
     * 列表配置
     */
    private Map<String, Object> listConfig;
    /**
     * 其它元数据配置
     */
    private Map<String, Object> metadata;
    /**
     * 排序
     */
    private String sort;
}
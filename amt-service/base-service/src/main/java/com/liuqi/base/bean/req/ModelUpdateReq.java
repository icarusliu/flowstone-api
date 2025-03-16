package com.liuqi.base.bean.req;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 模型更新对象 
 * @author Coder Generator 2025-03-14 12:45:23 
 **/
@Data
public class ModelUpdateReq {
    private String id;
    private String code;
    /**
     * 模型名称
     */
    private String name;
    /**
     * 状态，0：草稿；1：已应用；2：已修改；3：已下线
     */
    private Integer status;
    /**
     * 分类id
     */
    private String typeId;
    /**
     * 排序
     */
    private String sort;
    /**
     * 元数据
     */
    private Map<String, Object> metadata;

    /**
     * 字段
     */
    private List<ModelFieldUpdateReq> fields;

    /**
     * 列表配置
     */
    private Map<String, Object> listConfig;
    /**
     * 列表字段配置
     */
    private List<Map<String, Object>> listFields;
    /**
     * 表单配置
     */
    private Map<String, Object> formConfig;
    /**
     * 表单字段配置
     */
    private List<Map<String, Object>> formFields;
}
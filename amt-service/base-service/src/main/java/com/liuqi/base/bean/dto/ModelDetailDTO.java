package com.liuqi.base.bean.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 模型详情
 * @author Coder Generator 2025-03-14 12:45:23 
 **/
@Data
public class ModelDetailDTO extends ModelDTO {
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
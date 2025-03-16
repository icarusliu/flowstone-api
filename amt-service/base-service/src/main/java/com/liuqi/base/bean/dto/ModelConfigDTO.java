package com.liuqi.base.bean.dto;

import com.liuqi.common.base.bean.dto.BaseDTO;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 模型配置数据实体 
 * @author Coder Generator 2025-03-15 16:48:44 
 **/
@Data
public class ModelConfigDTO extends BaseDTO {
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
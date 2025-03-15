package com.liuqi.dua.bean.dto;

import com.liuqi.common.base.bean.dto.BaseDTO;
import liquibase.util.StringUtil;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 已发布模型数据实体 
 * @author Coder Generator 2025-03-15 10:50:27 
 **/
@Data
public class ModelPublishedDTO extends BaseDTO {
    /**
     * 编码
     */
    private String code;
    /**
     * 名称
     */
    private String name;
    /**
     * 元数据
     */
    private Map<String, Object> metadata;

    /**
     * 表单配置
     */
    private Map<String, Object> formConfig;

    /**
     * 列表配置
     */
    private Map<String, Object> listConfig;

    /**
     * 模型字段
     */
    private List<ModelFieldDTO> fields;

    public String getTableName() {
        return "td_" + StringUtil.toKabobCase(code).replace("-", "");
    }
}
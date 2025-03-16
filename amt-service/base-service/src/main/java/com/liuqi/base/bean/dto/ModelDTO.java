package com.liuqi.base.bean.dto;

import com.liuqi.common.base.bean.dto.BaseDTO;
import liquibase.util.StringUtil;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 模型数据实体 
 * @author Coder Generator 2025-03-14 12:45:23 
 **/
@Data
public class ModelDTO extends BaseDTO {
    /**
     * 模型编码
     */
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
     * 模型字段
     */
    private List<ModelFieldDTO> fields;

    public String getTableName() {
        return "td_" + StringUtil.toKabobCase(code).replace("-", "");
    }
}
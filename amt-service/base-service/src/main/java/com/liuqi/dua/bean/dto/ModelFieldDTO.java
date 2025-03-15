package com.liuqi.dua.bean.dto;

import com.liuqi.common.base.bean.dto.BaseDTO;
import liquibase.util.StringUtil;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.Optional;

/**
 * 模型字段数据实体 
 * @author Coder Generator 2025-03-14 12:59:51 
 **/
@Data
public class ModelFieldDTO extends BaseDTO {
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

    /**
     * 获取表字段名称
     * @return 表字段名称
     */
    public String getColumnName() {
        return StringUtil.toKabobCase(code).replace("-", "");
    }

    /**
     * 生成删除列语句
     */
    public String getColumnDropSql(String tableName) {
        return "alter table " + tableName + " drop column " + getColumnName() + ";";
    }

    /**
     * 生成表新建时的语句
     */
    public String getCreateSql() {
        return composeColumnSql();
    }

    private String composeColumnSql() {
        StringBuilder sb = new StringBuilder();
        sb.append(getColumnName())
                .append(" ")
                .append(dataType)
                .append(Optional.ofNullable(dataConfig).orElse(""))
                .append(!nullable ? " not null" : "");

        if (StringUtils.isNotBlank(defaultValue)) {
            sb.append(" default ");
            if ("varchar".equals(dataType)) {
                sb.append("'").append(defaultValue).append("'");
            } else {
                sb.append(defaultValue);
            }
        }

        // id 是内置字段，需要增加主键标志
        if (code.equals("id")) {
            sb.append(" primary key");
        }

        // 增加注释
        sb.append(" comment '")
                .append(name)
                .append("'");

        return sb.toString();
    }

    /**
     * 生成更新或新增语句
     * 不允许字段重命名
     */
    public String getColumnUpdateSql(String tableName, Boolean add) {
        return "alter table " + tableName +
                (add ? " add " : " modify") +
                " column " +
                composeColumnSql() +
                ";";
    }
}
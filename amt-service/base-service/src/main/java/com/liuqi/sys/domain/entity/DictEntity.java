package com.liuqi.sys.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.liuqi.sys.bean.DictItem;
import com.liuqi.common.base.domain.entity.BaseEntity;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 字典
 */
@Data
@TableName(value = "sys_dict", autoResultMap = true)
public class DictEntity extends BaseEntity {
    private String code;

    /**
     * 字典类型，items：列表字段，sql: sql字典
     */
    private String type;

    private String name;

    private Integer status;

    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<DictItem> items;

    private String remark;

    @TableField(typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> metadata;
}

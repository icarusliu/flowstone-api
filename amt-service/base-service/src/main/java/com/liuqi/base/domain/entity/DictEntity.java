package com.liuqi.base.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.liuqi.base.bean.DictItem;
import com.liuqi.common.base.domain.entity.BaseEntity;
import lombok.Data;

import java.util.List;

/**
 * 字典
 */
@Data
@TableName(value = "sys_dict", autoResultMap = true)
public class DictEntity extends BaseEntity {
    private String code;

    private String name;

    private Integer status;

    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<DictItem> items;

    private String remark;
}

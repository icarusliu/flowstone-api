package com.liuqi.base.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.FastjsonTypeHandler;
import com.liuqi.common.base.domain.entity.BaseEntity;
import lombok.Data;

import java.util.Map;

/**
 * 模型字段
 *
 * @author  LiuQi 2025/3/14-12:46
 * @version V1.0
 **/
@Data
@TableName(value = "d_model_field", autoResultMap = true)
public class ModelFieldEntity extends BaseEntity {
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
     * 是否主键
     */
    private Boolean primaryKey;

    /**
     * 数据配置（如varchar后的长度等）
     */
    private String dataConfig;

    /**
     * 默认值
     */
    private String defaultValue;

    /**
     * 关联字典
     */
    private String dictCode;

    /**
     * 其它元数据配置
     */
    @TableField(typeHandler = FastjsonTypeHandler.class)
    private Map<String, Object> metadata;

    /**
     * 排序
     */
    private String sort;
}

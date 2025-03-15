package com.liuqi.dua.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.FastjsonTypeHandler;
import com.liuqi.common.base.domain.entity.BaseEntity;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 模型配置
 *
 * @author  LiuQi 2025/3/15-16:43
 * @version V1.0
 **/
@Data
@TableName(value = "b_model_config", autoResultMap = true)
public class ModelConfigEntity extends BaseEntity {
    /**
     * 列表配置
     */
    @TableField(typeHandler = FastjsonTypeHandler.class)
    private Map<String, Object> listConfig;

    /**
     * 列表字段配置
     */
    @TableField(typeHandler = FastjsonTypeHandler.class)
    private List<Map<String, Object>> listFields;

    /**
     * 表单配置
     */
    @TableField(typeHandler = FastjsonTypeHandler.class)
    private Map<String, Object> formConfig;

    /**
     * 表单字段配置
     */
    @TableField(typeHandler = FastjsonTypeHandler.class)
    private List<Map<String, Object>> formFields;
}

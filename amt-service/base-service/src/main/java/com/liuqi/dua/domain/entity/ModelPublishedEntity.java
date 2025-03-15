package com.liuqi.dua.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.FastjsonTypeHandler;
import com.liuqi.common.base.domain.entity.BaseEntity;
import com.liuqi.dua.bean.dto.ModelFieldDTO;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 已发布模型
 *
 * @author  LiuQi 2025/3/15-10:48
 * @version V1.0
 **/
@Data
@TableName(value = "d_model_published", autoResultMap = true)
public class ModelPublishedEntity extends BaseEntity {
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
    @TableField(typeHandler = FastjsonTypeHandler.class)
    private Map<String, Object> metadata;

    /**
     * 模型字段
     */
    @TableField(typeHandler = FastjsonTypeHandler.class)
    private List<ModelFieldDTO> fields;
}

package com.liuqi.base.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.liuqi.common.base.domain.entity.BaseEntity;
import lombok.Data;

/**
 * 模型分类
 *
 * @author LiuQi 2025/3/14-12:15
 * @version V1.0
 **/
@Data
@TableName("d_model_type")
public class ModelTypeEntity extends BaseEntity {
    /**
     * 模型分类名称
     */
    private String name;

    /**
     * 排序
     */
    private String sort;

    /**
     * 父级分类id
     */
    private String parentId;
}

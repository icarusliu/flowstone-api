package com.liuqi.dua.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.FastjsonTypeHandler;
import com.liuqi.common.base.domain.entity.BaseEntity;
import lombok.Data;

import java.util.Map;

/**
 * 模型
 *
 * @author  LiuQi 2025/3/14-12:17
 * @version V1.0
 **/
@Data
@TableName("d_model")
public class ModelEntity extends BaseEntity {
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
    @TableField(typeHandler = FastjsonTypeHandler.class)
    private Map<String, Object> metadata;
}

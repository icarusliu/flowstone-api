package com.liuqi.dua.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.liuqi.common.annotations.Comment;
import com.liuqi.common.base.domain.entity.BaseEntity;
import lombok.Data;

/**
 * 接口分类
 *
 * @author  LiuQi 2024/8/9-22:06
 * @version V1.0
 **/
@Data
@Comment("接口分类")
@TableName("d_api_type")
public class ApiTypeEntity extends BaseEntity {
    private String name;
    private Integer sort;
    private String parentId;
}

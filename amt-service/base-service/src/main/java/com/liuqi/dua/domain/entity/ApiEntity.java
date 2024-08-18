package com.liuqi.dua.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.liuqi.common.annotations.Comment;
import com.liuqi.common.base.domain.entity.BaseEntity;
import lombok.Data;

/**
 * 接口实体类
 *
 * @author  LiuQi 2024/7/8-22:29
 * @version V1.0
 **/
@Data
@TableName("d_api")
@Comment("接口")
public class ApiEntity extends BaseEntity {
    private String content;
    private String name;
    private String remark;
    private String path;
    private String method;
    private String typeId;
}

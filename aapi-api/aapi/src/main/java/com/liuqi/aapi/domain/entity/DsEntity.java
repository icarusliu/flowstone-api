package com.liuqi.aapi.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.liuqi.common.annotations.Comment;
import com.liuqi.common.base.domain.entity.BaseEntity;
import lombok.Data;

/**
 * 数据源配置
 *
 * @author LiuQi 8:57
 **/
@Data
@TableName("a_api_ds")
@Comment("数据源配置表")
public class DsEntity extends BaseEntity {
    private String name;

    private String type;

    private String url;

    private String username;

    private String password;
}

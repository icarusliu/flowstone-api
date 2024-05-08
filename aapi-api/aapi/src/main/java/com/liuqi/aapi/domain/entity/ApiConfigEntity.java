package com.liuqi.aapi.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.liuqi.common.annotations.Comment;
import com.liuqi.common.base.domain.entity.BaseEntity;
import lombok.Data;

/**
 * API配置
 *
 * @author LiuQi 14:06
 **/
@Data
@TableName("a_api_config")
@Comment("API配置表")
public class ApiConfigEntity extends BaseEntity {
    private String name;

    private String path;

    private String method;

    private Integer cacheMinute;

    private Integer status;

    private String typeId;

    private String nodes;

    private String outputConfig;

    private String testConfig;

    private String remark;
}

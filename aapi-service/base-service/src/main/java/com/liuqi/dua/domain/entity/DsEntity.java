package com.liuqi.dua.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.liuqi.common.annotations.Comment;
import com.liuqi.common.base.domain.entity.BaseEntity;
import lombok.Data;

/**
 * 数据源
 *
 * @author  LiuQi 2024/8/9-11:39
 * @version V1.0
 **/
@Data
@Comment("数据源配置")
@TableName("d_ds")
public class DsEntity extends BaseEntity {
    private String code;
    private String name;
    private String type;
    private String url;
    private String username;
    private String password;
}

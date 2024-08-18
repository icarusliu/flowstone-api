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
    @Comment("编码")
    private String code;
    @Comment("名称")
    private String name;
    @Comment("分类")
    private String type;
    @Comment("地址")
    private String url;
    @Comment("用户名")
    private String username;
    @Comment("密码")
    private String password;
}

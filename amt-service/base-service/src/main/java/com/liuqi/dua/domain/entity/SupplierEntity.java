package com.liuqi.dua.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.liuqi.common.annotations.Comment;
import com.liuqi.common.base.domain.entity.BaseEntity;
import lombok.Data;

/**
 * 接入方
 *
 * @author  LiuQi 2024/8/12-18:56
 * @version V1.0
 **/
@Data
@Comment("接入方")
@TableName("d_api_supplier")
public class SupplierEntity extends BaseEntity {
    @Comment("名称")
    private String name;

    @Comment("路径")
    private String url;

    @Comment("备注")
    private String remark;

    @Comment("鉴权配置")
    private String authConfig;
}


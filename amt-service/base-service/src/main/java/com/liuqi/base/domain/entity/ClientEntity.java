package com.liuqi.base.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.liuqi.common.annotations.Comment;
import com.liuqi.common.base.domain.entity.BaseEntity;
import lombok.Data;

/**
 * 客户端
 *
 * @author  LiuQi 2024/9/25-8:57
 * @version V1.0
 **/
@Data
@Comment("客户端")
@TableName("base_client")
public class ClientEntity extends BaseEntity {
    @Comment("名称")
    private String name;

    @Comment("密钥")
    private String secret;

    @Comment("是否停用，0：否，1：是")
    private Boolean disabled;

    @Comment("描述")
    private String remark;

    @Comment("应用白名单")
    private String whiteIps;

    @Comment("应用支持每s请求数")
    private Integer limitInSecond;

    @Comment("是否支持访问所有接口")
    private Boolean withAllApis;
}

package com.liuqi.dua.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.liuqi.common.annotations.Comment;
import com.liuqi.common.base.domain.entity.BaseEntity;
import lombok.Data;

/**
 * 客户端接口
 *
 * @author  LiuQi 2024/9/25-15:46
 * @version V1.0
 **/
@Data
@Comment("客户端接口列表")
@TableName("d_client_api")
public class ClientApiEntity extends BaseEntity {
    @Comment("客户端id")
    private String clientId;

    @Comment("接口id")
    private String apiId;
}

package com.liuqi.dua.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.liuqi.common.annotations.Comment;
import com.liuqi.common.base.domain.entity.BaseEntity;
import lombok.Data;

/**
 * 接口草稿
 *
 * @author  LiuQi 2024/7/8-22:32
 * @version V1.0
 **/
@Comment("接口草稿")
@TableName("d_api_draft")
@Data
public class ApiDraftEntity extends BaseEntity {
    private Boolean guestMode;
    private String content;
    private String name;
    private String remark;
    private String path;
    private String method;
    private String typeId;
    private Integer status;
}

package com.liuqi.common.base.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.liuqi.common.annotations.Comment;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

@Data
public class BaseEntity {
    @Id
    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    @Comment("租户编号")
    private String tenantId;

    @Comment("创建用户")
    private String createUser;

    @Comment("创建时间")
    private LocalDateTime createTime;

    @Comment("更新用户")
    private String updateUser;

    @Comment("更新时间")
    private LocalDateTime updateTime;

    @Comment("是否删除")
    private Boolean deleted;
}

package com.liuqi.base.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.liuqi.common.base.domain.entity.BaseEntity;
import lombok.Data;

@Data
@TableName("base_user_role")
public class UserRoleEntity extends BaseEntity {
    private String userId;

    private String roleId;
}

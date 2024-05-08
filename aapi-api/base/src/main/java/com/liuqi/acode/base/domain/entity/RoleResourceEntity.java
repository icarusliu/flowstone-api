package com.liuqi.acode.base.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.liuqi.common.base.domain.entity.BaseEntity;
import lombok.Data;

@Data
@TableName("base_role_resource")
public class RoleResourceEntity extends BaseEntity {
    private String roleId;

    private Integer resourceType;

    private String resourceId;
}

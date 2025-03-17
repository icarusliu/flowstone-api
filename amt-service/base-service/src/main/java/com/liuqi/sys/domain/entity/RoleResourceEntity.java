package com.liuqi.sys.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.liuqi.common.base.domain.entity.BaseEntity;
import lombok.Data;

@Data
@TableName("sys_role_resource")
public class RoleResourceEntity extends BaseEntity {
    private String roleId;

    /**
     * 资源类型，menu/button/api
     */
    private String resourceType;

    /**
     * 资源id，菜单id、按钮id或者接口id
     */
    private String resourceId;
}

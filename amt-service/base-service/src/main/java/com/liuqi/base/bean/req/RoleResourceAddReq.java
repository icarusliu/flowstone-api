package com.liuqi.base.bean.req;

import lombok.Data;

@Data
public class RoleResourceAddReq {
    private String roleId;
    private String resourceId;
    private String resourceType;
}
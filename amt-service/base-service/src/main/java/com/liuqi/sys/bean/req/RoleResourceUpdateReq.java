package com.liuqi.sys.bean.req;

import lombok.Data;

@Data
public class RoleResourceUpdateReq {
    private String id;
    private String roleId;
    private String resourceId;
    private String resourceType;
}
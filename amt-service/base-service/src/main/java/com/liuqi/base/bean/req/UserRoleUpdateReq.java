package com.liuqi.base.bean.req;

import lombok.Data;

@Data
public class UserRoleUpdateReq {
    private String id;

    private String roleId;

    private String userId;
}
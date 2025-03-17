package com.liuqi.sys.bean.req;

import lombok.Data;

@Data
public class UserRoleAddReq {
    private String userId;

    private String roleId;
}
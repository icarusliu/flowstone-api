package com.liuqi.common.utils;

import lombok.Data;

/**
 * 用户信息上下文
 */
@Data
public class UserContext {
    private String tenantId;

    private String userId;

    private String username;

    private String displayName;

    private Boolean isSuperAdmin;

    private String password;

    public UserContext(String tenantId, String userId, String username, String displayName, Boolean isSuperAdmin) {
        this.tenantId = tenantId;
        this.userId = userId;
        this.username = username;
        this.displayName = displayName;
        this.isSuperAdmin = isSuperAdmin;
    }
}

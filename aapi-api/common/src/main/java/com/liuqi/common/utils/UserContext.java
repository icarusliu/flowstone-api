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

    public UserContext(String tenantId, String userId, String username, String displayName) {
        this.tenantId = tenantId;
        this.userId = userId;
        this.username = username;
        this.displayName = displayName;
    }
}

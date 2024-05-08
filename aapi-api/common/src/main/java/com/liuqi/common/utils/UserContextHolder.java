package com.liuqi.common.utils;

import java.util.Optional;

/**
 * 用户上下文存储器
 */
public class UserContextHolder {
    private static final ThreadLocal<UserContext> holder = new ThreadLocal<>();

    public static void set(UserContext userContext) {
        holder.set(userContext);
    }

    public static void clear() {
        holder.remove();
    }

    public static Optional<UserContext> get() {
        return Optional.ofNullable(holder.get());
    }

    public static Optional<String> getUserId() {
        return get().map(UserContext::getUserId);
    }

    public static Optional<String> getTenantId() {
        return get().map(UserContext::getTenantId);
    }

    public static Optional<String> getUsername() {
        return get().map(UserContext::getUsername);
    }
}

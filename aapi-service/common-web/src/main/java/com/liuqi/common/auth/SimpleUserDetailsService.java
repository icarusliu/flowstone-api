package com.liuqi.common.auth;

import com.liuqi.common.utils.UserContext;

import java.util.Optional;

/**
 * 简单鉴权用户服务
 *
 * @author LiuQi 18:10
 **/
public interface SimpleUserDetailsService {
    Optional<UserContext> getUserDetails(String username);
}

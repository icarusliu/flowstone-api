package com.liuqi.common.config.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

/**
 * 应用认证实现
 * 覆盖默认的，提供异常次数校验等功能
 *
 * @author  LiuQi 2024/8/15-11:58
 * @version V1.0
 **/
public class AppAuthenticationProvider extends DaoAuthenticationProvider {
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        // 用户登录异常次数检查 TODO

        try {
            Authentication result = super.authenticate(authentication);
            return result;
        } catch (Exception ex) {
            // 保存用户异常次数  TODO

            throw ex;
        }
    }
}

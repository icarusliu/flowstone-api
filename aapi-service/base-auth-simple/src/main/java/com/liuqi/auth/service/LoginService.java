package com.liuqi.auth.service;

import com.liuqi.auth.exception.AuthErrorCodes;
import com.liuqi.auth.utils.AuthUtils;
import com.liuqi.common.auth.SimpleUserDetailsService;
import com.liuqi.common.exception.AppException;
import com.liuqi.common.utils.UserContext;
import com.liuqi.common.utils.UserContextHolder;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * 用户登录服务
 *
 * @author LiuQi 18:03
 **/
@Service
public class LoginService {
    @Autowired
    private SimpleUserDetailsService userDetailsService;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private HttpServletResponse response;

    @Value("${application.security.domain:ngq.com}")
    private String domain;

    public String login(String username, String password) {
        UserContext userContext = userDetailsService.getUserDetails(username)
                .orElseThrow(() -> AppException.of(AuthErrorCodes.USERNAME_OR_PASSWORD_ERROR));
        if (!password.equals(userContext.getPassword())) {
            throw AppException.of(AuthErrorCodes.USERNAME_OR_PASSWORD_ERROR);
        }

        // 生成token
        String token = AuthUtils.generateToken(userContext);

        // 设置Cookie
        Cookie cookie = new Cookie("access_token", token);
        cookie.setDomain(domain);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        response.addCookie(cookie);

        return token;
    }

    public void logout() {
        Cookie cookie = new Cookie("access_token", "");
        cookie.setDomain(domain);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        UserContextHolder.clear();
    }
}

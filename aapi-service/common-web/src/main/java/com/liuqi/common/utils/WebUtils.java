package com.liuqi.common.utils;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.Optional;

/**
 * WEB辅助类
 *
 * @author LiuQi 17:13
 **/
@Slf4j
public class WebUtils {
    /**
     * 解析请求中的token
     * @param request 请求信息
     * @return 解析到的token
     */
    public static Optional<String> getToken(HttpServletRequest request) {
        if (null == request.getCookies()) {
            return Optional.ofNullable(request.getHeader("authorization"))
                    .map(str -> str.replace("Bearer ", ""));
        }

        String token = Arrays.stream(request.getCookies())
                .filter(cookie -> cookie.getName().equals("access_token"))
                .findFirst()
                .map(Cookie::getValue).orElseGet(() -> {
                    // 如果cookie中没有，则从authorization中获取
                    return Optional.ofNullable(request.getHeader("authorization"))
                            .map(str -> str.replace("Bearer ", ""))
                            .orElse(null);
                });

        if (log.isDebugEnabled()) {
            log.debug("Token, {}", token);
        }

        return Optional.ofNullable(token);
    }
}

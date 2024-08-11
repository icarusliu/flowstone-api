package com.liuqi.auth.config;

import com.liuqi.auth.utils.AuthUtils;
import com.liuqi.common.annotations.AuthAdmin;
import com.liuqi.common.annotations.NoAuth;
import com.liuqi.common.utils.UserContext;
import com.liuqi.common.utils.UserContextHolder;
import com.liuqi.common.utils.WebUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 鉴权过滤器
 *
 * @author LiuQi 17:11
 **/
@Slf4j
public class SecurityHandler implements HandlerInterceptor {


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = WebUtils.getToken(request)
                .orElse(null);
        boolean isSuperAdmin = false;

        if (null != token) {
            UserContext userContext = AuthUtils.parse(token);
            isSuperAdmin = userContext.getIsSuperAdmin();
            UserContextHolder.set(userContext);
        }

        if (handler instanceof HandlerMethod handlerMethod) {
            if (handlerMethod.hasMethodAnnotation(NoAuth.class)) {
                return true;
            }

            if (null == token) {
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                return false;
            }

            if (handlerMethod.hasMethodAnnotation(AuthAdmin.class) && !isSuperAdmin) {
                // 判断当前用户是否是管理员
                response.setStatus(HttpStatus.FORBIDDEN.value());
                return false;
            }
        }

        return HandlerInterceptor.super.preHandle(request, response, handler);
    }
}

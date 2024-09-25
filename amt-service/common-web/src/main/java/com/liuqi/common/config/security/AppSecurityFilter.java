package com.liuqi.common.config.security;

import com.liuqi.common.bean.UserContext;
import com.liuqi.common.bean.UserContextHolder;
import com.liuqi.common.utils.AuthUtils;
import com.liuqi.common.utils.WebUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * 应用安全拦截器
 *
 * @author  LiuQi 2024/8/15-13:44
 * @version V1.0
 **/
@Component
public class AppSecurityFilter extends OncePerRequestFilter {
    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 提取token，存储用户信息
        String token = WebUtils.getToken(request)
                .orElse(null);

        if (StringUtils.isBlank(token)) {
            filterChain.doFilter(request, response);
            return;
        }

        UserContext userContext = AuthUtils.parse(token);
        if (!userContext.getIsClient()) {
            String username = userContext.getUsername();
            userContext = (UserContext) userDetailsService.loadUserByUsername(username);
            UserContextHolder.set(userContext);
        }

        Authentication authentication = UsernamePasswordAuthenticationToken.authenticated(userContext, null, userContext.getAuthorities());
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);

        filterChain.doFilter(request, response);
    }
}

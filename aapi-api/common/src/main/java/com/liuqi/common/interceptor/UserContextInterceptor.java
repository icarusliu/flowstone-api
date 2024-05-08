package com.liuqi.common.interceptor;

import cn.hutool.json.JSONObject;
import cn.hutool.jwt.JWT;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.liuqi.common.utils.UserContextHolder;
import com.liuqi.common.utils.UserContext;
import com.liuqi.common.exception.UnauthorizedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.CollectionUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Optional;

@Component
@Slf4j
@Order(0)
public class UserContextInterceptor implements HandlerInterceptor {
    @Value("#{'${application.security.ignored-urls:}'.split(',')}")
    private List<String> ignoredUrls;

    private static final AntPathMatcher matcher = new AntPathMatcher();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String url = request.getRequestURI();
        if (log.isDebugEnabled()) {
            log.debug("Received request, context path: {}", url);
        }

        if (!CollectionUtils.isEmpty(ignoredUrls)) {
            log.debug("Ignored urls: {}", ignoredUrls);
            boolean ignored = ignoredUrls.stream().anyMatch(item -> matcher.match(item, url));
            if (ignored) {
                if (log.isDebugEnabled()) {
                    log.debug("Url ignored, url: {}", url);
                }
                return true;
            }
        }

        String token = Optional.ofNullable(WebUtils.getCookie(request, "access_token"))
                .map(Cookie::getValue)
                .orElse(null);
        if (StringUtils.isBlank(token)) {
            log.error("Token does not exists!");
            throw new UnauthorizedException();
        }

        token = token.replace("Bearer ", "")
                .replaceAll(" ", "");

        if (log.isDebugEnabled()) {
            log.debug("Token: {}", token);
        }

        try {
            JWT jwt = JWT.of(token);
            JSONObject obj = jwt.getPayloads();
            String username = obj.getStr("user_name");
            String tenantId = obj.getStr("tenantId");
            JSONObject user = obj.getJSONObject("user");
            String userId = user.getStr("id");
            String displayName = user.getStr("displayName");
            UserContextHolder.set(new UserContext(tenantId, userId, username, displayName));
        } catch (Exception ex) {
            log.error("Parse token failed", ex);
            throw new UnauthorizedException();
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        UserContextHolder.clear();
    }
}

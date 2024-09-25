package com.liuqi.common.interceptor;

import com.liuqi.base.bean.dto.ClientDTO;
import com.liuqi.base.service.ClientService;
import com.liuqi.common.ApiHolder;
import com.liuqi.common.bean.UserContext;
import com.liuqi.common.bean.UserContextHolder;
import com.liuqi.dua.bean.dto.ApiDTO;
import com.liuqi.dua.bean.dto.ClientApiDTO;
import com.liuqi.dua.service.ClientApiService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.List;

/**
 * 客户端模式拦截器
 *
 * @author  LiuQi 2024/9/25-19:21
 * @version V1.0
 **/
@Component
@Slf4j
public class ClientInterceptor implements HandlerInterceptor {
    @Autowired
    private ClientService clientService;

    @Autowired
    private ClientApiService clientApiService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        ApiDTO api = ApiHolder.get();
        if (null == api || api.getGuestMode()) {
            return HandlerInterceptor.super.preHandle(request, response, handler);
        }

        // 以下一定是要登录过的

        // 检查是否是客户端模式
        UserContext userContext = UserContextHolder.get().orElse(null);
        if (null == userContext) {
            response.setStatus(401);
            return false;
        }

        if (!userContext.getIsClient()) {
            // 非客户端模式
            return HandlerInterceptor.super.preHandle(request, response, handler);
        }

        // 客户端模式，需要检查客户端是否有相应接口的权限
        ClientDTO client = clientService.findById(userContext.getUserId())
                .orElse(null);
        if (null == client) {
            response.setStatus(401);
            return false;
        }

        if (client.getWithAllApis()) {
            return HandlerInterceptor.super.preHandle(request, response, handler);
        }

        List<ClientApiDTO> list = clientApiService.findByClient(client.getId());
        boolean match = list.stream().anyMatch(item -> item.getApiId().equals(api.getId()));
        if (match) {
            return HandlerInterceptor.super.preHandle(request, response, handler);
        }

        log.error("客户端无权限调用相应接口，clientId: {}, api: {}, {}", client.getId(), api.getId(), api.getName());
        response.setStatus(401);
        return false;
    }
}

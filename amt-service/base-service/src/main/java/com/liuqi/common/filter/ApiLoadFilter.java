package com.liuqi.common.filter;

import com.liuqi.common.ApiHolder;
import com.liuqi.dua.bean.dto.ApiDTO;
import com.liuqi.dua.service.ApiDraftService;
import com.liuqi.dua.service.ApiService;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 接口加载拦截器
 * 加载接口信息并进行存储，接口后续处理时使用
 *
 * @author  LiuQi 2024/9/25-18:39
 * @version V1.0
 **/
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ApiLoadFilter implements Filter {
    @Autowired
    private ApiService apiService;

    @Autowired
    private ApiDraftService apiDraftService;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest servletRequest = (HttpServletRequest) request;
        String uri = servletRequest.getRequestURI();
        if (!uri.startsWith("/dua/") && !uri.startsWith("/dua-test/")) {
            chain.doFilter(request, response);
            return;
        }

        if (uri.startsWith("/dua/")) {
            String key = uri.substring(5);
            apiService.findByPath(key).ifPresent(ApiHolder::set);
            chain.doFilter(request, response);
        } else {
            String key = uri.substring(10);
            apiDraftService.findByPath(key).ifPresent(item -> {
                ApiDTO dto = new ApiDTO();
                BeanUtils.copyProperties(item, dto);
                ApiHolder.set(dto);
            });
            chain.doFilter(request, response);
        }
    }
}

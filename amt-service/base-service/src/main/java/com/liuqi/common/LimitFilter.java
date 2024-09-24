package com.liuqi.common;

import com.liuqi.common.utils.WebUtils;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 访问数量限制拦截器
 *
 * @author  LiuQi 2024/9/24-20:11
 * @version V1.0
 **/
@Component
public class LimitFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest servletRequest = (HttpServletRequest) request;
        String ip = WebUtils.getRequestHost(servletRequest);


        chain.doFilter(request, response);
    }
}

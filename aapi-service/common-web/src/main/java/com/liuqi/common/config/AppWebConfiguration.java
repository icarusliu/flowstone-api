package com.liuqi.common.config;

import com.liuqi.common.interceptor.UserContextInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * MVC配置
 */
@Configuration
public class AppWebConfiguration implements WebMvcConfigurer {
    @Autowired(required = false)
    private UserContextInterceptor userContextInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        if (null != userContextInterceptor) {
            registry.addInterceptor(userContextInterceptor)
                    .addPathPatterns("/**");
        }
    }
}

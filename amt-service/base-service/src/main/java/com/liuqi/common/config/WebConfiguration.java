package com.liuqi.common.config;

import com.liuqi.common.interceptor.ClientInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web配置
 *
 * @author  LiuQi 2024/9/25-19:24
 * @version V1.0
 **/
@Configuration
public class WebConfiguration implements WebMvcConfigurer {
    @Autowired
    private ClientInterceptor clientInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        WebMvcConfigurer.super.addInterceptors(registry);
        registry.addInterceptor(clientInterceptor)
                .addPathPatterns("/dua/**");
    }
}

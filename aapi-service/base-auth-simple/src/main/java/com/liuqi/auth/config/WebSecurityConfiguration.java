package com.liuqi.auth.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;
import java.util.List;

/**
 * 鉴权配置
 *
 * @author LiuQi 17:28
 **/
@Configuration
@EnableWebMvc
@Slf4j
public class WebSecurityConfiguration implements WebMvcConfigurer {
    @Value("${application.security.ignored-urls}")
    private String ignoredUrls;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        List<String> urls = Arrays.asList(ignoredUrls.split(","));
        log.info("Security ignored urls: {}", urls);
        registry.addInterceptor(new SecurityHandler())
                .addPathPatterns("/**")
                .excludePathPatterns(urls);
    }
}

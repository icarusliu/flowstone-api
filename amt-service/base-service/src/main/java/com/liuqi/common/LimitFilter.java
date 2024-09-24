package com.liuqi.common;

import com.google.common.util.concurrent.RateLimiter;
import com.liuqi.common.utils.WebUtils;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RRateLimiter;
import org.redisson.api.RateIntervalUnit;
import org.redisson.api.RateType;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 访问数量限制拦截器
 *
 * @author  LiuQi 2024/9/24-20:11
 * @version V1.0
 **/
@Component
@Slf4j
public class LimitFilter implements Filter {
    @Autowired(required = false)
    private RedissonClient redissonClient;

    @Value("${api.limitInSecond}")
    private Integer limitInSecond;

    private final ConcurrentHashMap<String, RateLimiter> rateLimiterCache = new ConcurrentHashMap<>(32);

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest servletRequest = (HttpServletRequest) request;
        String ip = WebUtils.getRequestHost(servletRequest);
        String key = "ipLimited:" + ip;

        boolean b;
        if (null == redissonClient) {
            // 使用guava进行流量控制
            RateLimiter limiter = rateLimiterCache.computeIfAbsent(key, (k) -> RateLimiter.create(limitInSecond));
            b = limiter.tryAcquire(1);
        } else {
            // 使用Redis进行流量控制
            RRateLimiter rateLimiter = redissonClient.getRateLimiter(key);
            rateLimiter.trySetRate(RateType.OVERALL, limitInSecond, 1, RateIntervalUnit.SECONDS);
            b = rateLimiter.tryAcquire(1);
        }

        if (b) {
            chain.doFilter(request, response);
            return;
        }

        log.error("IP({})请求次数超出限制", ip);
        HttpServletResponse servletResponse = (HttpServletResponse) response;
        servletResponse.setStatus(429);
    }
}

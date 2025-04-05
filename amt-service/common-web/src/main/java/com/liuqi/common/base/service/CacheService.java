package com.liuqi.common.base.service;

import com.alibaba.fastjson2.JSON;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * 缓存服务
 *
 * @author  LiuQi 2025/3/22-17:48
 * @version V1.0
 **/
@Service
public class CacheService {
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    public <T> List<T> getOrCacheList(String key, Class<T> clazz, Supplier<List<T>> supplier) {
        return this.getOrCacheList(key, clazz, supplier, 30);
    }

    public <T> List<T> getOrCacheList(String key, Class<T> clazz, Supplier<List<T>> supplier, Integer timeoutInMinute) {
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        String value = valueOperations.get(key);
        if (StringUtils.isBlank(value)) {
            List<T> list = supplier.get();
            if (null == list || list.isEmpty()) {
                return null;
            }
            if (null == timeoutInMinute) {
                valueOperations.set(key, JSON.toJSONString(list));
            } else {
                valueOperations.set(key, JSON.toJSONString(list), timeoutInMinute, TimeUnit.MINUTES);
            }
            return list;
        }

        return JSON.parseArray(value, clazz);
    }

    public <T> T getOrCache(String key, Class<T> clazz, Supplier<T> supplier, Integer timeoutInMinute) {
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        String value = valueOperations.get(key);
        if (StringUtils.isBlank(value)) {
            T t = supplier.get();
            if (null == t) {
                return null;
            }

            if (null == timeoutInMinute) {
                valueOperations.set(key, JSON.toJSONString(t));
            } else {
                valueOperations.set(key, JSON.toJSONString(t), timeoutInMinute, TimeUnit.MINUTES);
            }
            return t;
        }

        return JSON.parseObject(value, clazz);
    }

    public <T> T getOrCache(String key, Class<T> clazz, Supplier<T> supplier) {
        return this.getOrCache(key, clazz, supplier, 30);
    }

    public <T> T get(String key, Class<T> clazz) {
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        String value = valueOperations.get(key);
        if (StringUtils.isBlank(value)) {
            return null;
        }

        return JSON.parseObject(value, clazz);
    }

    public void cache(String key, Object value, Integer timeoutInMinutes) {
        if (null == value) {
            return;
        }

        if (null == timeoutInMinutes) {
            redisTemplate.opsForValue().set(key, JSON.toJSONString(value));
        } else {
            redisTemplate.opsForValue().set(key, JSON.toJSONString(value), timeoutInMinutes, TimeUnit.MINUTES);
        }
    }

    public void cache(String key, Object value) {
        this.cache(key, value, 30);
    }

    public void remove(String key) {
        redisTemplate.delete(key);
    }
}

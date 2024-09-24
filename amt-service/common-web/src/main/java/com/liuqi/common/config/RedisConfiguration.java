package com.liuqi.common.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.io.IOException;

/**
 * Redis配置
 *
 * @author  LiuQi 2024/9/24-21:42
 * @version V1.0
 **/
@ConditionalOnProperty(prefix = "spring.data.redis", name = "enabled", havingValue = "true")
@Configuration
public class RedisConfiguration {
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(connectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(Object.class));
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(new Jackson2JsonRedisSerializer<>(Object.class));
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

    @Bean(
            destroyMethod = "shutdown"
    )
    @ConditionalOnMissingBean({RedissonClient.class})
    public RedissonClient redisson(RedisProperties redisProperties) throws IOException {
        Config config = new Config();
        config.useSingleServer().setAddress("redis://" + redisProperties.getHost() + ":" + redisProperties.getPort())
                .setConnectTimeout((int) (redisProperties.getConnectTimeout().getSeconds() * 1000))
                .setDatabase(redisProperties.getDatabase())
                .setPassword(redisProperties.getPassword());

        return Redisson.create(config);
    }
}

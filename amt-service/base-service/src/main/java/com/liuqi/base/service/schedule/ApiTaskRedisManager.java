package com.liuqi.base.service.schedule;

import com.alibaba.fastjson2.JSON;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.Topic;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

/**
 * 基础服务Redis监听服务 
 *
 * @author  LiuQi 2025/5/16-22:23
 * @version V1.0
 **/
@Component
public class ApiTaskRedisManager implements MessageListener {
    public static final String TOPIC = "apiTask";
    @Autowired
    private RedisMessageListenerContainer redisMessageListenerContainer;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private ScheduleTaskExecutor scheduleTaskExecutor;

    @PostConstruct
    public void init() {
        Topic topic = new ChannelTopic(TOPIC);
        redisMessageListenerContainer.addMessageListener(this, topic);
    }

    @Override
    public void onMessage(Message message, @Nullable byte[] pattern) {
        String body = message.toString();
        Msg msg = JSON.parseObject(body, Msg.class);
        String id = msg.getId();
        switch (msg.op) {
            case "start": scheduleTaskExecutor.startTask(id); break;
            case "stop": scheduleTaskExecutor.stopTask(id); break;
            case "restart": scheduleTaskExecutor.restartTask(id); break;
        }
    }

    public void stopTask(String id) {
        this.send(new Msg("stop", id));
    }

    public void startTask(String id) {
        this.send(new Msg("start", id));
    }

    public void restartTask(String id) {
        this.send(new Msg("restart", id));
    }

    private void send(Msg msg) {
        redisTemplate.convertAndSend(TOPIC, JSON.toJSONString(msg));
    }

    @Data
    private static class Msg {
        private String op;
        private String id;

        public Msg(String op, String id) {
            this.op = op;
            this.id = id;
        }
    }
}

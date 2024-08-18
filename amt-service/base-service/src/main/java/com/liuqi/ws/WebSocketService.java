package com.liuqi.ws;

import com.alibaba.fastjson2.JSON;
import com.liuqi.common.bean.UserContextHolder;
import jakarta.websocket.Session;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * websocket服务
 *
 * @author LiuQi 2024/8/1-17:04
 * @version V1.0
 **/
@Service
@Slf4j
public class WebSocketService {

    private final Map<String, Session> userSession = new ConcurrentHashMap<>();

    /**
     * 接收到消息时，绑定用户与session的关系
     * 暂不处理前端其它消息
     *
     * @param session 会话
     * @param userId  用户id
     */
    public void onMessage(Session session, String userId) {
        if (StringUtils.isBlank(userId)) {
            return;
        }
        
        userSession.put(userId, session);
    }

    /**
     * 发送WS消息
     *
     * @param msg 消息内容
     */
    public void send(WebSocketMsg msg) {
        UserContextHolder.getUserId().ifPresent(userId -> sendToUser(userId, msg));
    }

    /**
     * 发送WS消息
     *
     * @param title 标题
     * @param type 消息类型
     * @param content 消息内容
     */
    private void send(String type, String level, String title, Object content) {
        WebSocketMsg msg = new WebSocketMsg();
        msg.setType(type);
        msg.setTitle(title);
        msg.setContent(content);
        msg.setLevel(level);
        this.send(msg);
    }

    private void send(String type, String level, String title) {
        this.send(type, level, title, null);
    }

    public void debug(String type, String title, Object content) {
        this.send(type, "debug", title, content);
    }

    public void debug(String type, String title) {
        this.send(type, "debug", title);
    }

    public void info(String type, String title) {
        this.send(type, "info", title);
    }

    public void info(String type, String title, Object content) {
        this.send(type, "info", title, content);
    }

    public void error(String type, String title, Object content) {
        this.send(type, "error", title, content);
    }

    public void error(String type, String title) {
        this.send(type, "error", title);
    }

    /**
     * 发送WS消息
     *
     * @param userId 用户id
     * @param msg    消息内容
     */
    public void sendToUser(String userId, WebSocketMsg msg) {
        Optional.ofNullable(userSession.get(userId))
                .ifPresent(session -> sendInternal(session, msg));
    }

    @Async
    private static void sendInternal(Session session, WebSocketMsg msg) {
        try {
            session.getBasicRemote().sendText(JSON.toJSONString(msg));
        } catch (IOException e) {
            log.error("发送WS消息失败", e);
        }
    }

    /**
     * 关闭连接
     *
     * @param session 会话
     */
    synchronized public void onClose(Session session) {
        for (String userId : userSession.keySet()) {
            Session pSession = userSession.get(userId);
            if (pSession == session) {
                userSession.remove(userId);
                return;
            }
        }
    }
}

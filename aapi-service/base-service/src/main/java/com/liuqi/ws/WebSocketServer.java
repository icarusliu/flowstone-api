package com.liuqi.ws;

import com.alibaba.fastjson2.JSON;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;

/**
 * WebSocket
 *
 * @author  LiuQi 2024/8/1-16:02
 * @version V1.0
 **/
@ServerEndpoint("/ws/front")
@Slf4j
public class WebSocketServer {
    private Session session;

    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        log.info("接收到客户端连接：{}", session.getId());

        try {
            sendMessage("success");
        } catch (IOException e) {
            log.error("发送消息失败", e);
        }
    }

    @OnClose
    public void onClose() {
        log.info("客户端连接断开");
        WebSocketService webSocketService = WebsocketConfiguration.applicationContext.getBean(WebSocketService.class);
        webSocketService.onClose(session);
    }

    @OnMessage
    public void onMessage(String message) {
        if (StringUtils.isBlank(message)) {
            return;
        }
        WsMessageReq req = JSON.parseObject(message, WsMessageReq.class);
        String userId = req.getUserId();

        // 根据token获取用户信息
        WebSocketService webSocketService = WebsocketConfiguration.applicationContext.getBean(WebSocketService.class);
        webSocketService.onMessage(session, userId);
    }

    public void sendMessage(String msg) throws IOException {
        this.session.getBasicRemote().sendText(msg);
    }
}

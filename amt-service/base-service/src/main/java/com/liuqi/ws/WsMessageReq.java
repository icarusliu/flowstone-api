package com.liuqi.ws;

import lombok.Data;

/**
 * WebSocket消息
 *
 * @author  LiuQi 2024/8/1-17:00
 * @version V1.0
 **/
@Data
public class WsMessageReq {
    private String userId;
    private String msg;
}

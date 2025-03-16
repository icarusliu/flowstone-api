package com.liuqi.base.bean;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * WebSocket消息
 *
 * @author  LiuQi 2024/9/20-20:17
 * @version V1.0
 **/
@Data
public class WebSocketMessage {
    /**
     * 消息类型
     */
    private String type;

    /**
     * 消息标题
     */
    private String title;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 其它参数
     */
    private Map<String, Object> params = new HashMap<>(16);

    public WebSocketMessage addParam(String key, Object value) {
        params.put(key, value);
        return this;
    }
}

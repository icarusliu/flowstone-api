package com.liuqi.ws;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * WebSocket消息
 *
 * @author  LiuQi 2024/8/14-9:29
 * @version V1.0
 **/
@Data
public class WebSocketMsg {
    // 消息类型
    private String type;

    // 消息级别
    private String level = "info";

    // 标题
    private String title;

    // 消息内容
    private Object content;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime time = LocalDateTime.now();
}

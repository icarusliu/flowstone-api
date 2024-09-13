package com.liuqi.dua.executor.bean;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.liuqi.common.bean.UserContext;
import com.liuqi.dua.bean.dto.ApiDTO;
import com.liuqi.ws.WebSocketMsg;
import com.liuqi.ws.WebSocketService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Data;
import org.springframework.context.ApplicationContext;
import org.springframework.lang.Nullable;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * API运行上下文
 *
 * @author LiuQi 2024/8/9-9:36
 * @version V1.0
 **/
@Data
public class ApiExecutorContext {
    // 接口信息
    private ApiDTO apiInfo;

    // 接口请求对象，可以为空
    @JsonIgnore
    @Nullable
    private HttpServletRequest request;

    // 请求返回对象，可以为空
    @JsonIgnore
    @Nullable
    private HttpServletResponse response;

    // 接口请求参数
    private Map<String, Object> requestParams = new HashMap<>();

    // 接口请求头信息
    @JsonIgnore
    private Map<String, String> requestHeaders = new HashMap<>();

    // 接口请求Cookie
    private Map<String, String> requestCookies = new HashMap<>(16);

    // 节点信息
    private List<NodeInfo> nodes;

    // 执行异常列表
    @JsonIgnore
    private Map<String, Exception> exceptions = new HashMap<>();

    // 执行结果
    private Map<String, Object> nodeOutputs = new ConcurrentHashMap<>(16);

    // Spring运行上下文
    @JsonIgnore
    private ApplicationContext applicationContext;

    // 是否是在进行接口测试
    private Boolean isTest;

    // WebSocket消息推送服务
    @JsonIgnore
    private WebSocketService webSocketService;

    // 用户上下文
    @JsonIgnore
    private UserContext userContext;

    /**
     * 推送常规消息
     *
     * @param title 消息标题
     */
    public void info(String title) {
        this.info(title, null);
    }

    /**
     * 推送常规消息
     *
     * @param title 标题
     * @param obj   消息内容
     */
    public void info(String title, Object obj) {
        if (null == userContext || !isTest) {
            return;
        }

        WebSocketMsg webSocketMsg = new WebSocketMsg();
        webSocketMsg.setType("apiTest");
        webSocketMsg.setTitle(title);
        webSocketMsg.setLevel("info");
        webSocketMsg.setContent(obj);

        webSocketService.sendToUser(userContext.getUserId(), webSocketMsg);
    }

    /**
     * 推送异常信息
     *
     * @param title 标题
     */
    public void error(String title) {
        this.error(title, null);
    }

    /**
     * 推送异常信息
     *
     * @param title 标题
     * @param obj   消息内容
     */
    public void error(String title, Object obj) {
        if (null == userContext || !isTest) {
            return;
        }

        WebSocketMsg webSocketMsg = new WebSocketMsg();
        webSocketMsg.setType("apiTest");
        webSocketMsg.setTitle(title);
        webSocketMsg.setLevel("error");
        webSocketMsg.setContent(obj);

        webSocketService.sendToUser(userContext.getUserId(), webSocketMsg);
    }

    public void setRequest(@Nullable HttpServletRequest request) {
        this.request = request;

        if (null == request) {
            return;
        }

        // 解析请求头信息
        if (null == RequestContextHolder.getRequestAttributes()) {
            // 内部调用时，request不为空，但实际没有请求，会报错
            return;
        }

        Enumeration<String> enumeration = request.getHeaderNames();
        while (enumeration.hasMoreElements()) {
            String key = enumeration.nextElement();
            String value = request.getHeader(key);
            this.requestHeaders.put(key, value);
        }

        Cookie[] cookies = request.getCookies();
        if (null != cookies) {
            for (Cookie cookie : cookies) {
                this.requestCookies.put(cookie.getName(), cookie.getValue());
            }
        }
    }

    public void putOutput(String code, Object result) {
        if (null == result) {
            return;
        }

        this.nodeOutputs.put(code, result);
    }
}

package com.liuqi.dua.executor.bean;

import com.liuqi.dua.bean.dto.ApiDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Data;
import org.springframework.lang.Nullable;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * API运行上下文
 *
 * @author  LiuQi 2024/8/9-9:36
 * @version V1.0
 **/
@Data
public class ApiExecutorContext {
    // 接口信息
    private ApiDTO apiInfo;

    // 接口请求对象，可以为空
    @Nullable private HttpServletRequest request;

    // 请求返回对象，可以为空
    @Nullable private HttpServletResponse response;

    // 接口请求参数
    private Map<String, Object> requestParams = new HashMap<>();

    // 接口请求头信息
    private Map<String, String> requestHeaders = new HashMap<>();

    // 节点信息
    private List<NodeInfo> nodes;

    // 执行异常列表
    private Map<String, Exception> exceptions = new HashMap<>();

    // 执行结果
    private Map<String, Object> nodeOutputs = new ConcurrentHashMap<>(16);

    public void setRequest(@Nullable HttpServletRequest request) {
        this.request = request;

        if (null == request) {
            return;
        }

        // 解析请求头信息
        Enumeration<String> enumeration = request.getHeaderNames();
        while (enumeration.hasMoreElements()) {
            String key = enumeration.nextElement();
            String value = request.getHeader(key);
            this.requestHeaders.put(key, value);
        }
    }
}

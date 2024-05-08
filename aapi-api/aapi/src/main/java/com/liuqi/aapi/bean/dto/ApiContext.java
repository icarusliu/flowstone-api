package com.liuqi.aapi.bean.dto;

import lombok.Data;
import org.springframework.context.ApplicationContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * API运行环境
 *
 * @author LiuQi 15:50
 **/
@Data
public class ApiContext {
    private HttpServletRequest request;

    private HttpServletResponse response;

    private ApiParams params;

    // 默认参数，每个节点执行时候的默认参数，从请求中获取
    private Map<String, Object> defParams;

    private ApplicationContext applicationContext;


    /**
     * 组装运行上下文
     */
    public static ApiContext parse(HttpServletRequest request, HttpServletResponse response,
                                   Map<String, Object> params,
                                   Object body,
                                   Map<String, Object> pathVariables) {
        ApiContext context = new ApiContext();
        context.request = request;
        context.response = response;

        // 解析请示参数
        ApiParams apiParams = new ApiParams();
        context.params = apiParams;
        ApiParams.Request requestParams = new ApiParams.Request();
        apiParams.setRequest(requestParams);
        requestParams.setParams(params);
        requestParams.setBody(body);
        requestParams.setPathParams(pathVariables);

        // 处理节点参数，默认情况下，将输入的参数全部添加到参数中，然后再将处理节点定义的参数
        Map<String, Object> defParams = new HashMap<>(16);
        context.defParams = defParams;
        defParams.putAll(params);
        defParams.putAll(pathVariables);

        // 处理body参数
        if (body instanceof Map<?, ?>) {
            // body只处理map的情况
            defParams.putAll((Map<? extends String, ?>) body);
        }

        return context;
    }

    public static void main(String[] args) {
    }
}

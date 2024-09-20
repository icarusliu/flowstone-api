package com.liuqi.dua.executor.tasks.http;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.http.Method;
import com.alibaba.fastjson2.JSON;
import com.liuqi.common.GroovyUtils;
import com.liuqi.common.JsUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 认证处理
 *
 * @author  LiuQi 2024/9/14-13:32
 * @version V1.0
 **/
@Slf4j
public class AuthProcessor {
    /**
     * 处理接口认证
     */
    public static RequestContext processAuth(HttpTask task) {
        AuthConfig authConfig = task.getNodeConfig().getAuth();
        String authType = authConfig.getType();
        RequestContext requestContext = new RequestContext();
        if ("static".equals(authType)) {
            // 静态认证
            staticAuth(task, authConfig, requestContext);
            return requestContext;
        }

        // 动态认证
        String url = authConfig.getAuthUrl();
        String method = authConfig.getDynamicType();
        HttpRequest request = HttpUtil.createRequest(method.equals("get") ? Method.GET : Method.POST, url);
        List<AuthParam> params = authConfig.getDynamicParams();
        Map<String, Object> finalParams = new HashMap<>(16);
        if (!CollectionUtils.isEmpty(params)) {
            params.forEach(param -> finalParams.put(param.getKey(), param.getValue()));
        }

        if ("get".equals(method)) {
            request.form(finalParams);
        } else {
            request.body(JSON.toJSONString(finalParams));
        }

        HttpResponse response = request.execute();
        if (log.isInfoEnabled()) {
            log.debug("认证返回：{}", response);
        }
        if (!response.isOk()) {
            log.error("认证失败，返回结果：{}", response.body());
            throw new RuntimeException("接口认证失败");
        }

        String body = response.body();
        Map<String, Object> bodyMap = new HashMap<>(16);
        if (StringUtils.isNotBlank(body)) {
            try {
                bodyMap = JSON.parseObject(body);
            } catch (Exception ignored) {
            }
        }
        Map<String, Object> finalBodyMap = bodyMap;

        // 脚本执行参数组装
        Map<String, Object> executeParams = new HashMap<>(16);
        if (StringUtils.isNotBlank(body)) {
            executeParams.put("body", JSON.parse(body));
        } else {
            executeParams.put("body", new HashMap<>(16));
        }
        Map<String, String> headers = new HashMap<>(16);
        executeParams.put("headers", headers);
        response.headers().forEach((k, l) -> {
            if (l.isEmpty()) {
                return;
            }
            headers.put(k, l.get(0));
        });
        Map<String, String> cookies = new HashMap<>(16);
        executeParams.put("cookie", cookies);
        response.getCookies().forEach(cookie -> cookies.put(cookie.getName(), cookie.getValue()));

        authConfig.getTargetParams().forEach(param -> {
            if (null == param.getValue() || "".equals(param.getValue())) {
                return;
            }

            String key = param.getKey();
            String type = param.getType();
            String value = param.getValue().toString();
            Object val = switch (type) {
                case "const" -> param.getValue();
                case "cookie" -> response.getCookie(value);
                case "headers" -> response.header(value);
                case "body" -> {
                    if (value.equals("*")) {
                        yield value;
                    } else {
                        yield finalBodyMap.get(value);
                    }
                }
                case "js" -> JsUtils.execute(value, executeParams);
                case "groovy" -> GroovyUtils.execute(value, executeParams);
                default -> throw new IllegalStateException("Unexpected value: " + type);
            };

            if (null == val) {
                return;
            }

            String prefix = param.getPrefix();
            if (StringUtils.isNotBlank(prefix)) {
                val = prefix + " " + val;
            }

            requestContext.add(param.getTarget(), key, val);
        });

        return requestContext;
    }

    /**
     * 静态认证
     *
     * @param authConfig     认证配置
     * @param requestContext 参数
     */
    private static void staticAuth(HttpTask task, AuthConfig authConfig, RequestContext requestContext) {
        List<AuthParam> params = authConfig.getParams();
        if (CollectionUtils.isEmpty(params)) {
            return;
        }

        params.forEach(param -> {
            // 解析参数
            Pair<Object, Object> pair = task.getNodeParamValue(param);
            String key = param.getKey();
            Object val = pair.getLeft();

            if (null == val) {
                return;
            }

            // 根据target决定放到cookie还是请求体还是请求头还是查询参数中
            String target = param.getTarget();
            requestContext.add(target, key, val);
        });

    }
}

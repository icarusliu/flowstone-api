package com.liuqi.dua.executor.tasks.http;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.http.Method;
import com.alibaba.fastjson2.JSON;
import com.liuqi.common.ErrorCodes;
import com.liuqi.common.exception.AppException;
import com.liuqi.dua.bean.dto.SupplierDTO;
import com.liuqi.dua.executor.AbstractDagTask;
import com.liuqi.dua.executor.bean.ApiExecutorContext;
import com.liuqi.dua.executor.bean.NodeInput;
import com.liuqi.dua.service.SupplierService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringSubstitutor;
import org.springframework.lang.Nullable;

import java.net.HttpCookie;
import java.util.*;

/**
 * Http任务
 *
 * @author LiuQi 2024/8/9-9:54
 * @version V1.0
 **/
@Slf4j
public class HttpTask extends AbstractDagTask<HttpNodeConfig> {
    public HttpTask(ApiExecutorContext apiExecutorContext, NodeInput input) {
        super(apiExecutorContext, input);
    }

    /**
     * Framework would call this method, when it comes for tasks to be executed.
     *
     * @return the result of task execution
     */
    @Override
    public Object executeInternal() {
        HttpNodeConfig nodeConfig = this.nodeConfig;

        String supplier = nodeConfig.getSupplier();
        SupplierService supplierService = this.getSupplierService();
        SupplierDTO supplierDTO = supplierService.findById(supplier).orElseThrow(() -> AppException.of(ErrorCodes.API_SUPPLIER_NOT_EXISTS));
        String url = supplierDTO.getUrl() + nodeConfig.getPath();
        String method = nodeConfig.getMethod();

        // 解析路径参数
        Map<String, Object> pathVariables = this.getNodeParamValues(nodeConfig.getPathVariables());
        StringSubstitutor ss = new StringSubstitutor(pathVariables, "{", "}");
        url = ss.replace(url);

        this.info("准备进行接口调用，url", url);

        // 先进行认证规则处理
        String auth = nodeConfig.getAuth();
        RequestContext requestContext = null;
        if (StringUtils.isNotBlank(auth)) {
            String authConfig = supplierDTO.getAuthConfig();
            if (StringUtils.isNotBlank(authConfig)) {
                List<AuthConfig> authConfigList = JSON.parseArray(authConfig, AuthConfig.class);
                AuthConfig config = authConfigList.stream().filter(item -> auth.equals(item.getId()))
                        .findAny()
                        .orElseThrow(() -> new RuntimeException("认证规则不存在"));
                requestContext = AuthProcessor.processAuth(this, config);
            }
        }

        if (!nodeConfig.getBatch()) {
            // 非批量处理
            return runSingle(method, nodeConfig, url, requestContext);
        } else {
            return runBatch(method, nodeConfig, url, requestContext);
        }
    }

    /**
     * 组装请求头
     *
     * @return 组装的请求头
     */
    private Map<String, String> getRequestHeaders(RequestContext requestContext) {
        Map<String, Object> headers = this.getNodeParamValues(nodeConfig.getHeaders());
        Map<String, String> finalHeaders = new HashMap<>(16);
        headers.forEach((k, v) -> {
            if (null == v) {
                return;
            }

            finalHeaders.put(k, v.toString());
        });

        if (null != requestContext) {
            finalHeaders.putAll(requestContext.getHeaders());
        }

        this.info("headers", finalHeaders);
        return finalHeaders;
    }

    /**
     * 批量处理
     *
     * @param method     方法
     * @param nodeConfig 节点配置
     * @param url        路径
     * @return 处理结果
     */
    private Object runBatch(String method, HttpNodeConfig nodeConfig, String url, RequestContext requestContext) {
        // 解析请求头
        Map<String, String> finalHeaders = getRequestHeaders(requestContext);

        Map<String, Object> params;
        if (method.equals("get") || method.equals("delete")) {
            params = this.getNodeParamValues(nodeConfig.getParams());
        } else {
            params = this.getNodeParamValues(nodeConfig.getBody());
        }

        // 需要进行批量处理，那么需要有*的参数，并且参数值应当是列表
        if (!params.containsKey("*")) {
            this.error("批量处理时，参数中必须存在名称为*的参数");
            throw new RuntimeException("接口调用失败，接口配置异常");
        }

        Object listObj = params.get("*");
        if (!(listObj instanceof Collection<?> collection)) {
            this.error("批量处理时，参数*对应的参数值不是列表");
            throw new RuntimeException("接口调用失败，无效的调用参数");
        }

        this.info("开始分批进行接口调用");

        List<Object> result = new ArrayList<>(16);

        collection.forEach(obj -> {
            HttpRequest request;
            if (method.equals("get") || method.equals("delete")) {
                // 查询参数需要是Map类型参数
                if (!(obj instanceof Map)) {
                    throw new RuntimeException("接口调用失败，无效的调用参数");
                }

                if (method.equals("get")) {
                    request = HttpUtil.createGet(url);
                } else {
                    request = HttpUtil.createRequest(Method.DELETE, url);
                }

                Map<String, Object> form = (Map<String, Object>) obj;
                if (null != requestContext) {
                    form.putAll(requestContext.getParams());
                }

                this.info("参数", form);
                request.form(form);
            } else {
                if (method.equals("post")) {
                    request = HttpUtil.createPost(url);
                } else {
                    request = HttpUtil.createRequest(Method.PUT, url);
                }

                if (obj instanceof Map && null != requestContext) {
                    Map<String, Object> body = (Map<String, Object>) obj;
                    body.putAll(requestContext.getBody());
                    this.info("参数", body);
                    request.body(JSON.toJSONString(body));
                } else {
                    this.info("参数", obj);
                    request.body(JSON.toJSONString(obj));
                }
            }

            request.addHeaders(finalHeaders);

            // 处理Cookie
            if (null != requestContext) {
                requestContext.getCookie().forEach((k, v) -> request.cookie(new HttpCookie(k, v)));
            }

            // 增加请求方的cookie
            executorContext.getRequestCookies().forEach((k, v) -> request.cookie(new HttpCookie(k, v)));

            HttpResponse response = request.execute();
            if (!response.isOk()) {
                log.error("Http请求失败，返回结果：{}, {}", response.getStatus(), response.body());
                this.error("Http接口调用失败");
                this.error("返回状态", response.getStatus());
                this.error("返回结果", response.body());
                throw new RuntimeException(response.body());
            }

            String body = response.body();
            this.info("接口调用成功，返回结果", body);

            if (StringUtils.isBlank(body)) {
                result.add(body);
            } else {
                result.add(JSON.parse(body));
            }
        });

        this.info("批量接口调用完成，结果", result);

        return result;
    }

    /**
     * 执行非批量处理
     *
     * @param method         方法
     * @param nodeConfig     节点配置
     * @param url            请求路径
     * @return 处理结果
     */
    @Nullable
    private Object runSingle(String method, HttpNodeConfig nodeConfig, String url, RequestContext requestContext) {
        // 解析请求头
        Map<String, String> finalHeaders = getRequestHeaders(requestContext);

        // 参数解析
        Map<String, Object> params;
        HttpRequest request;
        if (method.equals("get") || method.equals("delete")) {
            // 解析查询参数
            params = this.getNodeParamValues(nodeConfig.getParams());

            if (params.containsKey("*")) {
                params = (Map<String, Object>) params.get("*");
            }

            if (requestContext != null) {
                params.putAll(requestContext.getParams());
            }

            this.info("参数：", params);

            if (method.equals("get")) {
                request = HttpUtil.createGet(url);
            } else {
                request = HttpUtil.createRequest(Method.DELETE, url);
            }

            request.form(params);
        } else {
            params = this.getNodeParamValues(nodeConfig.getBody());
            Object body = params;
            if (params.containsKey("*")) {
                body = params.get("*");
            }

            if (body instanceof Map) {
                Map<String, Object> bodyMap = (Map<String, Object>) body;
                if (requestContext != null) {
                    bodyMap.putAll(requestContext.getBody());
                }
            }

            this.info("参数：", body);

            if (method.equals("post")) {
                request = HttpUtil.createPost(url);
            } else {
                request = HttpUtil.createRequest(Method.PUT, url);
            }

            request.body(JSON.toJSONString(body));
        }

        request.addHeaders(finalHeaders);

        executorContext.getRequestCookies().forEach((k, v) -> request.cookie(new HttpCookie(k, v)));
        if (null != requestContext) {
            requestContext.getCookie().forEach((k, v) -> request.cookie(new HttpCookie(k, v)));
        }

        HttpResponse response = request.execute();
        if (!response.isOk()) {
            log.error("Http请求失败，返回结果：{}, {}", response.getStatus(), response.body());
            this.error("Http接口调用失败");
            this.error("返回状态", response.getStatus());
            this.error("返回结果", response.body());
            throw new RuntimeException(response.body());
        }

        String body = response.body();
        this.info("接口调用成功，返回结果", body);

        if (StringUtils.isBlank(body)) {
            return body;
        }

        return JSON.parse(body);
    }

    /**
     * 获取接入方服务
     *
     * @return 接入方服务
     */
    private SupplierService getSupplierService() {
        return this.executorContext.getApplicationContext().getBean(SupplierService.class);
    }

    public static void main(String[] args) {
        String a = "{\"a\": 1}";
        System.out.println(JSON.parse(a));
    }
}

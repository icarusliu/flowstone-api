package com.liuqi.dua.executor.tasks.http;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
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

import java.util.HashMap;
import java.util.Map;

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

        // 解析请求头
        Map<String, Object> headers = this.getNodeParamValues(nodeConfig.getHeaders());
        Map<String, String> finalHeaders = new HashMap<>(16);
        headers.forEach((k, v) -> {
            if (null == v) {
                return;
            }

            finalHeaders.put(k, v.toString());
        });

        // 参数解析
        Map<String, Object> params;
        HttpRequest request;
        if (method.equals("get") || method.equals("delete")) {
            // 解析查询参数
            params = this.getNodeParamValues(nodeConfig.getParams());
            if (params.containsKey("*")) {
                params = (Map<String, Object>) params.get("*");
            }

            request = HttpUtil.createGet(url)
                    .addHeaders(finalHeaders)
                    .form(params);
        } else {
            params = this.getNodeParamValues(nodeConfig.getBody());
            Object body = params;
            if (params.containsKey("*")) {
                body = params.get("*");
            }

            request = HttpUtil.createPost(url)
                    .addHeaders(finalHeaders)
                    .body(JSON.toJSONString(body));
        }

        HttpResponse response = request.execute();
        if (!response.isOk()) {
            log.error("Http请求失败，返回结果：{}, {}",  response.getStatus(), response.body());
            throw new RuntimeException(response.body());
        }

        String body = response.body();
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

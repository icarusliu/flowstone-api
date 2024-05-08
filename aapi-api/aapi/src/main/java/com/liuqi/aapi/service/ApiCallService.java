package com.liuqi.aapi.service;

import com.alibaba.fastjson.JSON;
import com.liuqi.aapi.bean.dto.ApiConfigDTO;
import com.liuqi.aapi.bean.dto.ApiContext;
import com.liuqi.aapi.bean.dto.ApiNode;
import com.liuqi.aapi.executors.Executors;
import com.liuqi.aapi.executors.ResultResolver;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * API调用服务
 *
 * @author LiuQi 14:40
 **/
@Service
@Slf4j
public class ApiCallService implements ApplicationContextAware {
    @Autowired
    private ApiConfigService apiConfigService;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private HttpServletResponse response;
    private ApplicationContext applicationContext;

    public Object call(String tenantId, String path, Map<String, Object> params, Object body, Map<String, Object> pathVariables) {
        Optional<ApiConfigDTO> configDTOOptional = apiConfigService.findByPath(tenantId, path);
        if (configDTOOptional.isEmpty()) {
            log.error("Api does not exists, path: {}", path);
            response.setStatus(404);
            return null;
        }

        ApiConfigDTO configDTO = configDTOOptional.get();

        // 组装运行环境
        ApiContext context = ApiContext.parse(request, response, params, body, pathVariables);
        context.setApplicationContext(applicationContext);

        String config = configDTO.getNodes();
        List<ApiNode> nodes = JSON.parseArray(config, ApiNode.class);
        if (!CollectionUtils.isEmpty(nodes)) {
            if (nodes.size() == 1) {
                // 只有一个节点，直接执行
                Executors.execute(context, nodes.get(0));
            } else {
                // 通过DAG执行器执行

            }
        }

        return ResultResolver.resolve(context, configDTO.getOutputConfig());
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}

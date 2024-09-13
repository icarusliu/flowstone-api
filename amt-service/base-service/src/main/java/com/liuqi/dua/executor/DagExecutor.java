package com.liuqi.dua.executor;

import com.alibaba.fastjson2.JSON;
import com.github.dexecutor.core.DefaultDexecutor;
import com.github.dexecutor.core.DexecutorConfig;
import com.github.dexecutor.core.ExecutionConfig;
import com.github.dexecutor.core.graph.Node;
import com.liuqi.common.ErrorCodes;
import com.liuqi.common.exception.AppException;
import com.liuqi.common.bean.UserContextHolder;
import com.liuqi.dua.bean.dto.ApiDTO;
import com.liuqi.dua.executor.bean.ApiConfig;
import com.liuqi.dua.executor.bean.ApiExecutorContext;
import com.liuqi.dua.executor.bean.NodeInfo;
import com.liuqi.dua.executor.bean.NodeInput;
import com.liuqi.dua.service.ApiLogService;
import com.liuqi.ws.WebSocketService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * DAG执行器
 *
 * @author LiuQi 2024/8/6-12:30
 * @version V1.0
 **/
@Component
@Slf4j
public class DagExecutor implements ApplicationContextAware {
    private static final ExecutorService executionService = Executors.newFixedThreadPool(10);

    @Autowired(required = false)
    private HttpServletRequest request;

    @Autowired(required = false)
    private HttpServletResponse response;

    private ApplicationContext applicationContext;

    @Autowired
    private WebSocketService webSocketService;

    @Autowired
    private ApiLogService apiLogService;

    /**
     * 执行接口任务
     *
     * @param api           接口信息
     * @param requestParams 请求参数
     * @param isTest        是否是在接口测试中
     * @return 处理结果
     */
    public Object execute(ApiDTO api, Map<String, Object> requestParams, boolean isTest) {
        long startTime = System.currentTimeMillis();
        String content = api.getContent();
        if (StringUtils.isEmpty(content)) {
            apiLogService.addErrorLog(api, requestParams, "接口定义异常，节点配置为空");
            if (isTest) {
                webSocketService.error("apiTest", "接口内容体为空");
            }
            throw AppException.of(ErrorCodes.API_CONTENT_EMPTY);
        }

        // 解析节点列表
        ApiConfig apiConfig = JSON.parseObject(content, ApiConfig.class);
        List<NodeInfo> nodeInfoList = apiConfig.getNodes();
        if (CollectionUtils.isEmpty(nodeInfoList)) {
            apiLogService.addErrorLog(api, requestParams, "接口定义异常，节点配置为空");
            if (isTest) {
                webSocketService.error("apiTest", "接口未配置任何节点");
            }
            throw AppException.of(ErrorCodes.API_NODES_EMPTY);
        }

        // 处理上下文
        ApiExecutorContext context = new ApiExecutorContext();
        context.setRequest(request);
        context.setResponse(response);
        context.setApiInfo(api);
        context.setNodes(nodeInfoList);
        context.setRequestParams(requestParams);
        context.setApplicationContext(applicationContext);
        context.setIsTest(isTest);
        context.setWebSocketService(webSocketService);
        UserContextHolder.get().ifPresent(context::setUserContext);

        if (isTest) {
            webSocketService.info("apiTest", "接口测试开始");
            webSocketService.info("apiTest", "接口上下文", context);
        }

        // 转换成任务输入对象
        List<NodeInput> nodes = nodeInfoList.stream().map(node -> {
            NodeInput nodeInput = new NodeInput();
            nodeInput.setNodeInfo(node);
            return nodeInput;
        }).collect(Collectors.toList());

        DagExecutionListener listener = new DagExecutionListener(context);
        DexecutorConfig<NodeInput, Object> config = new DexecutorConfig<>(executionService, new DagTaskProvider(context), listener);
        DefaultDexecutor<NodeInput, Object> executor = new DefaultDexecutor<>(config);

        // 构建依赖
        buildDepends(nodes, executor);

        // 执行
        executor.execute(ExecutionConfig.TERMINATING);

        // 如果存在异常，则进行抛出
        List<Exception> exceptions = listener.getExceptions();
        if (!CollectionUtils.isEmpty(exceptions)) {
            if (isTest) {
                webSocketService.error("apiTest", "接口执行失败");
            }

            apiLogService.addErrorLog(api, requestParams, "接口执行失败", exceptions);
            throw AppException.of(ErrorCodes.API_CALL_ERROR);
        }

        // 获取结果，默认取最后的节点的数据做返回数据
        // 如果有多个节点没有子节点，那么返回的数据是几个节点的返回数据组成的Map
        Collection<Node<NodeInput, Object>> processedNodes = config.getDexecutorState().getProcessedNodes();
        List<Node<NodeInput, Object>> endNodes = processedNodes.stream().filter(node -> CollectionUtils.isEmpty(node.getOutGoingNodes()))
                .toList();
        long spentTime = System.currentTimeMillis() - startTime;
        if (endNodes.size() == 1) {
            // 只有一个最终节点，将其结果做返回结果
            Object result = endNodes.get(0).getResult();
            if (isTest) {
                webSocketService.info("apiTest", "接口执行成功，返回结果", result);
                webSocketService.info("apiTest", "执行耗时", spentTime);
            }

            apiLogService.addSuccessLog(api, requestParams, result, spentTime);

            return result;
        } else {
            // 多个节点，那么以节点编码做key，组成成Map返回
            Map<String, Object> result = new HashMap<>(16);

            endNodes.forEach(node -> {
                Object nodeResult = node.getResult();
                String nodeCode = node.getValue().getNodeInfo().getCode();
                if (null != nodeResult) {
                    result.put(nodeCode, nodeResult);
                }
            });

            if (isTest) {
                webSocketService.info("apiTest", "接口执行成功，返回结果", result);
                webSocketService.info("apiTest", "执行耗时", spentTime);
            }

            apiLogService.addSuccessLog(api, requestParams, result, spentTime);

            return result;
        }
    }

    /**
     * 构建依赖关系
     *
     * @param nodes    任务列表
     * @param executor 执行器
     */
    private void buildDepends(Collection<NodeInput> nodes, DefaultDexecutor<NodeInput, Object> executor) {
        Map<String, NodeInput> taskMap = nodes
                .stream()
                .collect(Collectors.toMap(item -> item.getNodeInfo().getId(), i -> i));
        nodes.forEach(node -> {
            List<String> parentIds = node.getNodeInfo().getParentIds();
            if (CollectionUtils.isEmpty(parentIds)) {
                executor.addIndependent(node);
                return;
            }
            parentIds.forEach(parentId -> {
                NodeInput parent = taskMap.get(parentId);
                if (null != parent) {
                    executor.addDependency(parent, node);
                } else {
                    executor.addIndependent(node);
                }
            });
        });
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}

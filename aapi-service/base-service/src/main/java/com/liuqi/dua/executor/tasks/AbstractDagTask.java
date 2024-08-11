package com.liuqi.dua.executor.tasks;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.alibaba.fastjson2.JSON;
import com.github.dexecutor.core.task.Task;
import com.liuqi.common.base.bean.query.FilterOpConverter;
import com.liuqi.dua.executor.bean.ApiExecutorContext;
import com.liuqi.dua.executor.bean.NodeInfo;
import com.liuqi.dua.executor.bean.NodeInput;
import com.liuqi.dua.executor.bean.NodeParam;
import com.liuqi.dua.executor.tasks.impl.SqlTask;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 抽象任务实现
 *
 * @author LiuQi 2024/8/9-9:47
 * @version V1.0
 **/
public abstract class AbstractDagTask<T> extends Task<NodeInput, Object> {
    // 节点个性配置
    protected T nodeConfig;

    public AbstractDagTask(NodeInput input) {
        this.setId(input);
        Map<String, Object> config = input.getNodeInfo().getConfig();
        if (null != config) {
            ParameterizedType type = (ParameterizedType) this.getClass().getGenericSuperclass();
            Class<T> clazz = (Class<T>) type.getActualTypeArguments()[0];
            this.nodeConfig = JSON.parseObject(JSON.toJSONString(config), clazz);
        }

    }

    /**
     * 获取每个节点运行的参数
     * 包括请求参数与已运行节点的返回结果数据
     *
     * @return 节点运行的参数
     */
    protected Map<String, Object> getNodeExecuteParams() {
        ApiExecutorContext context = this.getId().getContext();
        Map<String, Object> outputs = context.getNodeOutputs();

        // 需要补充整个请求的参数
        Map<String, Object> params = new HashMap<>(outputs);
        Map<String, Object> requestParams = context.getRequestParams();
        if (null != requestParams) {
            params.put("request", requestParams);
        } else {
            params.put("request", new HashMap<>());
        }

        return params;
    }

    /**
     * 获取节点参数值
     *
     * @param nodeParam 节点参数配置
     * @return 节点参数值 left为第一个参数值，right为第二个参数值
     */
    protected Pair<Object, Object> getNodeParamValue(NodeParam nodeParam) {
        if (null == nodeParam) {
            return Pair.of(null, null);
        }

        String type = nodeParam.getType();
        Object value = nodeParam.getValue();
        Object value1 = nodeParam.getValue1();

        return switch (type) {
            case "const" -> Pair.of(value, value1);
            case "request" -> {
                nodeParam.setNodeCode("request");
                yield getNodeParamValueFromOtherNode(nodeParam);
            }

            // 节点数据，只能处理节点返回对象是Map的情况
            case "node" -> getNodeParamValueFromOtherNode(nodeParam);

            default -> Pair.of(null, null);
        };
    }

    /**
     * 从其它节点获取节点参数值
     *
     * @param nodeParam 节点参数配置
     * @return 节点参数值
     */
    private Pair<Object, Object> getNodeParamValueFromOtherNode(NodeParam nodeParam) {
        Map<String, Object> params = this.getNodeExecuteParams();
        String nodeCode = nodeParam.getNodeCode();
        if (StringUtils.isBlank(nodeCode)) {
            return Pair.of(null, null);
        }

        Object value, value1 = null;
        Object obj = params.get(nodeCode);
        if (obj instanceof Map<?, ?>) {
            // 只处理对象类型的节点数据
            Map<String, Object> map = (Map<String, Object>) obj;
            String key = Optional.ofNullable(nodeParam.getValue())
                    .map(Object::toString)
                    .orElse(nodeParam.getKey());
            value = map.get(key);
            if (null != nodeParam.getValue1()) {
                value1 = map.get(nodeParam.getValue1().toString());
            }

            return Pair.of(value, value1);
        }

        return Pair.of(null, null);
    }

    public static void main(String[] args) {
        NodeInfo nodeInfo = new NodeInfo();
        nodeInfo.setConfig(new HashMap<>(16));
        NodeInput input = new NodeInput();
        input.setNodeInfo(nodeInfo);
        SqlTask sqlTask = new SqlTask(input);
        System.out.println(sqlTask.nodeConfig);
    }
}

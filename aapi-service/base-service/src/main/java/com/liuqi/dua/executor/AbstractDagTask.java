package com.liuqi.dua.executor;

import com.alibaba.fastjson2.JSON;
import com.github.dexecutor.core.task.Task;
import com.liuqi.common.GroovyUtils;
import com.liuqi.common.JsUtils;
import com.liuqi.dua.executor.bean.ApiExecutorContext;
import com.liuqi.dua.executor.bean.NodeInput;
import com.liuqi.dua.executor.bean.NodeParam;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.ParameterizedType;
import java.util.*;

/**
 * 抽象任务实现
 *
 * @author LiuQi 2024/8/9-9:47
 * @version V1.0
 **/
public abstract class AbstractDagTask<T> extends Task<NodeInput, Object> {
    // 节点个性配置
    protected T nodeConfig;

    protected ApiExecutorContext executorContext;

    public AbstractDagTask(ApiExecutorContext executorContext, NodeInput input) {
        this.setId(input);
        this.executorContext = executorContext;

        Map<String, Object> config = input.getNodeInfo().getConfig();
        if (null != config) {
            ParameterizedType type = (ParameterizedType) this.getClass().getGenericSuperclass();
            Class<T> clazz = (Class<T>) type.getActualTypeArguments()[0];
            this.nodeConfig = JSON.parseObject(JSON.toJSONString(config), clazz);
        }
    }

    /**
     * Framework would call this method, when it comes for tasks to be executed.
     *
     * @return the result of task execution
     */
    @Override
    public Object execute() {
        this.info("节点执行开始，节点信息：", getId().getNodeInfo());

        Object result = this.executeInternal();

        this.info("节点执行结束，执行结果", result);

        String code = this.getId().getNodeInfo().getCode();
        executorContext.putOutput(code, result);

        return result;
    }

    /**
     * 推送WebSocket消息
     *
     * @param title 消息标题
     */
    protected void info(String title) {
        this.info(title, null);
    }

    /**
     * 推送WebSocket消息
     *
     * @param title 消息标题，
     * @param msg   消息内容
     */
    protected void info(String title, Object msg) {
        executorContext.info("【" + getId().getNodeInfo().getCode() + "】" + title, msg);
    }

    /**
     * 推送异常消息
     *
     * @param title 消息标题
     */
    protected void error(String title) {
        this.error(title, null);
    }

    /**
     * 推送异常消息
     *
     * @param title 标题
     * @param msg   内容
     */
    protected void error(String title, Object msg) {
        executorContext.error("【" + getId().getNodeInfo().getCode() + "】" + title, msg);
    }

    public abstract Object executeInternal();

    /**
     * 获取每个节点运行的参数
     * 包括请求参数与已运行节点的返回结果数据
     *
     * @return 节点运行的参数
     */
    protected Map<String, Object> getNodeExecuteParams() {
        ApiExecutorContext context = this.executorContext;
        Map<String, Object> outputs = context.getNodeOutputs();

        // 需要补充整个请求的参数
        Map<String, Object> params = new HashMap<>(outputs);
        Map<String, Object> requestParams = context.getRequestParams();

        params.put("request", Objects.requireNonNullElseGet(requestParams, HashMap::new));

        return params;
    }

    /**
     * 获取节点参数值，只处理参数一
     *
     * @param nodeParams 节点参数配置
     * @return 节点参数值
     */
    protected Map<String, Object> getNodeParamValues(List<NodeParam> nodeParams) {
        Map<String, Object> result = new HashMap<>(16);
        if (CollectionUtils.isEmpty(nodeParams)) {
            return result;
        }

        nodeParams.forEach(nodeParam -> {
            Object value = this.getNodeParamValue(nodeParam).getKey();
            if (null == value) {
                return;
            }

            result.put(nodeParam.getKey(), value);
        });

        return result;
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

            // js
            case "js" -> getJsValue(value, value1);

            // groovy
            case "groovy" -> getGroovyValue(value, value1);

            default -> Pair.of(null, null);
        };
    }

    /**
     * 获取Groovy类型的数据
     *
     * @param value  参数一，存储Groovy脚本内容
     * @param value1 参数二，存储参数二的Groovy脚本内容
     * @return JS类型值
     */
    private Pair<Object, Object> getGroovyValue(Object value, Object value1) {
        if (null == value) {
            return Pair.of(null, null);
        }

        Map<String, Object> params = this.getNodeExecuteParams();
        value = GroovyUtils.execute(value.toString(), params);
        if (null != value1) {
            value1 = GroovyUtils.execute(value1.toString(), params);
        }

        return Pair.of(value, value1);
    }

    /**
     * 获取JS类型的数据
     *
     * @param value  参数一，存储JS脚本内容
     * @param value1 参数二，存储参数二的JS脚本内容
     * @return JS类型值
     */
    private Pair<Object, Object> getJsValue(Object value, Object value1) {
        if (null == value) {
            return Pair.of(null, null);
        }

        Map<String, Object> params = this.getNodeExecuteParams();
        value = JsUtils.execute(value.toString(), params);
        if (null != value1) {
            value1 = JsUtils.execute(value1.toString(), params);
        }

        return Pair.of(value, value1);
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

    public T getNodeConfig() {
        return nodeConfig;
    }

    public ApiExecutorContext getExecutorContext() {
        return executorContext;
    }
}

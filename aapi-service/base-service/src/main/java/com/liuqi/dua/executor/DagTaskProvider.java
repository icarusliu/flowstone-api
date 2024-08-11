package com.liuqi.dua.executor;

import com.github.dexecutor.core.task.Task;
import com.github.dexecutor.core.task.TaskProvider;
import com.liuqi.common.ErrorCodes;
import com.liuqi.common.exception.AppException;
import com.liuqi.dua.executor.bean.NodeInfo;
import com.liuqi.dua.executor.bean.NodeInput;
import com.liuqi.dua.executor.tasks.impl.*;

/**
 * DAG任务生成器
 *
 * @author  LiuQi 2024/8/6-12:33
 * @version V1.0
 **/
public class DagTaskProvider implements TaskProvider<NodeInput, Object> {

    @Override
    public Task<NodeInput, Object> provideTask(NodeInput task) {
        NodeInfo nodeInfo = task.getNodeInfo();
        String nodeType = nodeInfo.getType().toLowerCase();
        return switch(nodeType) {
            case "sql" -> new SqlTask(task);
            case "js" -> new JsTask(task);
            case "groovy" -> new GroovyTask(task);
            case "http" -> new HttpTask(task);
            case "webservice" -> new WebServiceTask(task);
            case "kafka" -> new KafkaTask(task);
            case "table" -> new TableTask(task);
            default -> throw AppException.of(ErrorCodes.API_INVALID_NODE_TYPE);
        };
    }
}

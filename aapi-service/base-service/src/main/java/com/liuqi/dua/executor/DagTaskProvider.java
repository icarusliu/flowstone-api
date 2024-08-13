package com.liuqi.dua.executor;

import com.github.dexecutor.core.task.Task;
import com.github.dexecutor.core.task.TaskProvider;
import com.liuqi.common.ErrorCodes;
import com.liuqi.common.exception.AppException;
import com.liuqi.dua.executor.bean.ApiExecutorContext;
import com.liuqi.dua.executor.bean.NodeInfo;
import com.liuqi.dua.executor.bean.NodeInput;
import com.liuqi.dua.executor.tasks.groovy.GroovyTask;
import com.liuqi.dua.executor.tasks.http.HttpTask;
import com.liuqi.dua.executor.tasks.js.JsTask;
import com.liuqi.dua.executor.tasks.kafka.KafkaTask;
import com.liuqi.dua.executor.tasks.sql.SqlTask;
import com.liuqi.dua.executor.tasks.table.TableTask;
import com.liuqi.dua.executor.tasks.webservice.WebServiceTask;

/**
 * DAG任务生成器
 *
 * @author  LiuQi 2024/8/6-12:33
 * @version V1.0
 **/
public class DagTaskProvider implements TaskProvider<NodeInput, Object> {
    private final ApiExecutorContext apiExecutorContext;

    public DagTaskProvider(ApiExecutorContext apiExecutorContext) {
        this.apiExecutorContext = apiExecutorContext;
    }


    @Override
    public Task<NodeInput, Object> provideTask(NodeInput task) {
        NodeInfo nodeInfo = task.getNodeInfo();
        String nodeType = nodeInfo.getType().toLowerCase();
        return switch(nodeType) {
            case "sql" -> new SqlTask(apiExecutorContext, task);
            case "js" -> new JsTask(apiExecutorContext, task);
            case "groovy" -> new GroovyTask(apiExecutorContext, task);
            case "http" -> new HttpTask(apiExecutorContext, task);
            case "webservice" -> new WebServiceTask(apiExecutorContext, task);
            case "kafka" -> new KafkaTask(apiExecutorContext, task);
            case "table" -> new TableTask(apiExecutorContext, task);
            default -> throw AppException.of(ErrorCodes.API_INVALID_NODE_TYPE);
        };
    }
}

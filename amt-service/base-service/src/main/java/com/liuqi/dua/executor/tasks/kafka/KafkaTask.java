package com.liuqi.dua.executor.tasks.kafka;

import com.liuqi.dua.executor.bean.ApiExecutorContext;
import com.liuqi.dua.executor.bean.NodeInput;
import com.liuqi.dua.executor.AbstractDagTask;

/**
 * Kafka任务
 *
 * @author  LiuQi 2024/8/9-9:56
 * @version V1.0
 **/
public class KafkaTask extends AbstractDagTask {
    public KafkaTask(ApiExecutorContext apiExecutorContext, NodeInput input) {
        super(apiExecutorContext, input);
    }

    /**
     * Framework would call this method, when it comes for tasks to be executed.
     *
     * @return the result of task execution
     */
    @Override
    public Object executeInternal() {
        return null;
    }
}

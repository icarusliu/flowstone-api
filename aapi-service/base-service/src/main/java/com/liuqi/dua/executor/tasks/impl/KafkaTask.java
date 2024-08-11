package com.liuqi.dua.executor.tasks.impl;

import com.liuqi.dua.executor.bean.NodeInput;
import com.liuqi.dua.executor.tasks.AbstractDagTask;

/**
 * Kafka任务
 *
 * @author  LiuQi 2024/8/9-9:56
 * @version V1.0
 **/
public class KafkaTask extends AbstractDagTask {
    public KafkaTask(NodeInput input) {
        super(input);
    }

    /**
     * Framework would call this method, when it comes for tasks to be executed.
     *
     * @return the result of task execution
     */
    @Override
    public Object execute() {
        return null;
    }
}

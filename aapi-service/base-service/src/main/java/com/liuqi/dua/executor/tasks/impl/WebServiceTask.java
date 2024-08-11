package com.liuqi.dua.executor.tasks.impl;

import com.liuqi.dua.executor.bean.NodeInput;
import com.liuqi.dua.executor.tasks.AbstractDagTask;

/**
 * WebService任务
 *
 * @author  LiuQi 2024/8/9-9:55
 * @version V1.0
 **/
public class WebServiceTask extends AbstractDagTask {
    public WebServiceTask(NodeInput input) {
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

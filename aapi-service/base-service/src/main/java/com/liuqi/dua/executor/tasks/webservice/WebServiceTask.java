package com.liuqi.dua.executor.tasks.webservice;

import com.liuqi.dua.executor.bean.ApiExecutorContext;
import com.liuqi.dua.executor.bean.NodeInput;
import com.liuqi.dua.executor.AbstractDagTask;

/**
 * WebService任务
 *
 * @author  LiuQi 2024/8/9-9:55
 * @version V1.0
 **/
public class WebServiceTask extends AbstractDagTask {
    public WebServiceTask(ApiExecutorContext apiExecutorContext, NodeInput input) {
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

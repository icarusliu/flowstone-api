package com.liuqi.dua.executor;

import com.github.dexecutor.core.ExecutionListener;
import com.github.dexecutor.core.task.Task;
import com.liuqi.common.utils.ExceptionUtils;
import com.liuqi.dua.executor.bean.ApiExecutorContext;
import com.liuqi.dua.executor.bean.NodeInput;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 执行监听器
 * 记录成功与失败的数量
 *
 * @author LiuQi 2024/8/8-18:39
 * @version V1.0
 **/
public class DagExecutionListener implements ExecutionListener<NodeInput, Object> {
    private final AtomicInteger successCount = new AtomicInteger(0);
    private final AtomicInteger errorCount = new AtomicInteger(0);
    private final List<Exception> exceptions = new ArrayList<>(16);

    private final ApiExecutorContext executorContext;

    public DagExecutionListener(ApiExecutorContext executorContext) {
        this.executorContext = executorContext;
    }

    /**
     * Called on successful node execution
     *
     * @param task successfull one
     */
    @Override
    public void onSuccess(Task<NodeInput, Object> task) {
        successCount.incrementAndGet();
    }

    /**
     * Called on errored node execution
     *
     * @param task      the errored one
     * @param exception the exception caught
     */
    @Override
    public void onError(Task<NodeInput, Object> task, Exception exception) {
        executorContext.error("【" + task.getId().getNodeInfo().getCode() + "】节点执行异常：", ExceptionUtils.asString(exception));
        errorCount.incrementAndGet();
        exceptions.add(exception);
    }

    /**
     * 获取执行的所有任务数
     *
     * @return 执行的所有任务数
     */
    public int getTotal() {
        return successCount.get() + errorCount.get();
    }

    /**
     * 获取成功任务数
     *
     * @return 成功任务数
     */
    public int getSuccessCount() {
        return successCount.get();
    }

    /**
     * 获取异常任务数
     *
     * @return 异常任务数
     */
    public int getErrorCount() {
        return errorCount.get();
    }

    public List<Exception> getExceptions() {
        return this.exceptions;
    }
}

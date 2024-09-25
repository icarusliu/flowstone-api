package com.liuqi.dua.executor.tasks.groovy;

import com.liuqi.common.utils.GroovyUtils;
import com.liuqi.dua.executor.bean.ApiExecutorContext;
import com.liuqi.dua.executor.bean.NodeInput;
import com.liuqi.dua.executor.AbstractDagTask;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * Groovy任务
 *
 * @author  LiuQi 2024/8/9-9:52
 * @version V1.0
 **/
@Slf4j
public class GroovyTask extends AbstractDagTask<GroovyTaskConfig> {
    public GroovyTask(ApiExecutorContext apiExecutorContext, NodeInput input) {
        super(apiExecutorContext, input);
    }

    /**
     * Framework would call this method, when it comes for tasks to be executed.
     *
     * @return the result of task execution
     */
    @Override
    public Object executeInternal() {
        String script = this.nodeConfig.getGroovy();
        Map<String, Object> params = this.getNodeExecuteParams();

        this.info("Groovy脚本执行参数：{}", params);

        return GroovyUtils.execute(script, params);
    }
}

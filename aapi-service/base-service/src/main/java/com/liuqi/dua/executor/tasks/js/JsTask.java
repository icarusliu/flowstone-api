package com.liuqi.dua.executor.tasks.js;

import com.liuqi.common.JsUtils;
import com.liuqi.dua.executor.bean.ApiExecutorContext;
import com.liuqi.dua.executor.bean.NodeInput;
import com.liuqi.dua.executor.AbstractDagTask;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

/**
 * JS任务
 *
 * @author LiuQi 2024/8/9-9:50
 * @version V1.0
 **/
@Slf4j
public class JsTask extends AbstractDagTask<JsTaskConfig> {
    public JsTask(ApiExecutorContext apiExecutorContext, NodeInput input) {
        super(apiExecutorContext, input);
    }

    /**
     * Framework would call this method, when it comes for tasks to be executed.
     *
     * @return the result of task execution
     */
    @Override
    public Object executeInternal() {
        String js = this.nodeConfig.getJs();
        if (StringUtils.isBlank(js)) {
            return null;
        }

        Map<String, Object> params = this.getNodeExecuteParams();
        return JsUtils.execute(js, params);
    }
}

package com.liuqi.dua.executor.tasks.impl;

import com.liuqi.dua.executor.bean.config.GroovyTaskConfig;
import com.liuqi.dua.executor.bean.NodeInput;
import com.liuqi.dua.executor.tasks.AbstractDagTask;
import lombok.extern.slf4j.Slf4j;

import javax.script.Bindings;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

/**
 * Groovy任务
 *
 * @author  LiuQi 2024/8/9-9:52
 * @version V1.0
 **/
@Slf4j
public class GroovyTask extends AbstractDagTask<GroovyTaskConfig> {
    private static final ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
    private static final String FUNC_NAME = "func";

    public GroovyTask(NodeInput input) {
        super(input);
    }

    /**
     * Framework would call this method, when it comes for tasks to be executed.
     *
     * @return the result of task execution
     */
    @Override
    public Object execute() {
        ScriptEngine engine = scriptEngineManager.getEngineByName("groovy");
        Bindings binding = engine.createBindings();
        String groovy = this.nodeConfig.getGroovy();

        try {
            engine.eval(groovy, binding);
            return ((Invocable) engine).invokeFunction(FUNC_NAME, this.getNodeExecuteParams());
        } catch (Exception e) {
            log.error("执行groovy脚本异常, {}, {}", e.getMessage(), groovy);
            throw new RuntimeException("groovy脚本执行异常（" + e.getMessage() + ")", e);
        }
    }
}

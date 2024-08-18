package com.liuqi.common;

import lombok.extern.slf4j.Slf4j;

import javax.script.Bindings;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.util.Map;

/**
 * Groovy脚本辅助类
 *
 * @author LiuQi 2024/8/13-11:42
 * @version V1.0
 **/
@Slf4j
public class GroovyUtils {
    private static final ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
    private static final String FUNC_NAME = "func";

    /**
     * 执行Groovy脚本
     *
     * @param groovy 脚本内容
     * @param params 执行参数
     * @return 执行结果
     */
    public static Object execute(String groovy, Map<String, Object> params) {
        ScriptEngine engine = scriptEngineManager.getEngineByName("groovy");
        Bindings binding = engine.createBindings();

        try {
            engine.eval(groovy, binding);
            return ((Invocable) engine).invokeFunction(FUNC_NAME, params);
        } catch (Exception e) {
            log.error("执行groovy脚本异常, {}, {}", e.getMessage(), groovy);
            throw new RuntimeException("groovy脚本执行异常（" + e.getMessage() + ")", e);
        }
    }
}

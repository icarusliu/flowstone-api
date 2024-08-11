package com.liuqi.dua.executor.tasks.impl;

import com.alibaba.fastjson2.JSON;
import com.liuqi.dua.executor.bean.config.JsTaskConfig;
import com.liuqi.dua.executor.bean.NodeInput;
import com.liuqi.dua.executor.tasks.AbstractDagTask;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;

import java.util.Map;
import java.util.regex.Pattern;

/**
 * JS任务
 *
 * @author  LiuQi 2024/8/9-9:50
 * @version V1.0
 **/
@Slf4j
public class JsTask extends AbstractDagTask<JsTaskConfig> {
    private static final String FUNC_NAME = "func";

    public JsTask(NodeInput input) {
        super(input);
    }

    /**
     * Framework would call this method, when it comes for tasks to be executed.
     *
     * @return the result of task execution
     */
    @Override
    public Object execute() {
        String js = this.nodeConfig.getJs();
        if (StringUtils.isBlank(js)) {
            return null;
        }

        Map<String, Object> params = this.getNodeExecuteParams();

        long startTime = System.currentTimeMillis();
        Context context;
        Value value;

        context = Context.newBuilder().allowAllAccess(true).build();
        // 为兼容单独的JS语句、JS单函数定义、JS多函数定义等多场景，需要进行两次执行；
        String script = normalize(js);
        if (log.isDebugEnabled()) {
            log.debug("开始执行JS脚本：{}", script);
        }

        value = context.eval("js", script);

        if (log.isDebugEnabled()) {
            log.info("eval 耗时：{}", System.currentTimeMillis() - startTime);
        }
        startTime = System.currentTimeMillis();

        value = value.execute();

        if (log.isDebugEnabled()) {
            log.info("第一次执行耗时：{}", System.currentTimeMillis() - startTime);
        }
        startTime = System.currentTimeMillis();

        String result;
        if (value.canInvokeMember(FUNC_NAME)) {
            result = value.invokeMember(FUNC_NAME, params).toString();
        } else if (value.canExecute()) {
            result = value.execute(params)
                    .toString();
        } else {
            result = value.toString();
        }

        if (log.isDebugEnabled()) {
            log.info("第二次执行耗时：{}", System.currentTimeMillis() - startTime);
        }

        if (StringUtils.isBlank(result)) {
            return result;
        }

        try {
            if (result.startsWith("{")) {
                return JSON.parseObject(result);
            } else if (result.startsWith("[")) {
                return JSON.parseArray(result);
            }
        } catch (Exception ex) {
            log.warn("数据转换失败, {}", ex.getMessage());
            return result;
        }

        return result;
    }

    private static String normalize(String script) {
        if (StringUtils.isBlank(script)) {
            return script;
        }

        // 对执行脚本进行一次包装，兼容单独的语句、单函数、多函数、对象等不同场景
        return "() => {return " + processScript(script) + "}";
    }

    private static String processScript(String script) {
        // 先替换单行
        String regex = "//.*";
        Pattern pattern = Pattern.compile(regex);
        script = pattern.matcher(script).replaceAll("");

        // 再替换多行
        Pattern mPatter = Pattern.compile("/\\*.*?\\*/", Pattern.DOTALL);
        return mPatter.matcher(script).replaceAll("").trim();
    }

}

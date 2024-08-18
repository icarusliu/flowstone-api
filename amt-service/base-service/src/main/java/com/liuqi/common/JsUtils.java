package com.liuqi.common;

import com.alibaba.fastjson2.JSON;
import com.oracle.truffle.js.scriptengine.GraalJSScriptEngine;
import org.apache.commons.lang3.StringUtils;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.HostAccess;
import org.graalvm.polyglot.Value;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * JS工具类
 *
 * @author LiuQi 2024/8/13-11:37
 * @version V1.0
 **/
public class JsUtils {
    /**
     * 执行JS脚本
     *
     * @param js     脚本内容
     * @param params 参数
     * @return 执行结果
     */
    public static Object execute(String js, Map<String, Object> params) {
        if (StringUtils.isBlank(js)) {
            return null;
        }

        js = normalize(js).trim();

        HostAccess access = HostAccess.newBuilder(HostAccess.ALL)
                .targetTypeMapping(Value.class, Object.class, Value::hasArrayElements, v -> new LinkedList<>(v.as(List.class)))
                .targetTypeMapping(Value.class, Object.class, Value::hasMembers, v -> new HashMap<>(v.as(Map.class)))
                .build();
        ScriptEngine engine = GraalJSScriptEngine.create(null, Context.newBuilder("js")
                .allowHostAccess(access).allowAllAccess(true));
        String proxyFunc = " function proxyFunc(params) { params = JSON.parse(params); return func(params); }";
        if (js.contains("func(")) {
            js = js + proxyFunc;
        } else if (js.startsWith("(")) {
            js = "const func = " + js + "; " + proxyFunc;
        } else {
            js = "function func(params) { " + js + "} " + proxyFunc;
        }

        try {
            engine.eval(js);
            Invocable invocable = (Invocable) engine;
            return invocable.invokeFunction("proxyFunc", JSON.toJSONString(params));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static String normalize(String script) {
        if (StringUtils.isBlank(script)) {
            return script;
        }

        script = processScript(script).trim();
        return script;
    }

    /**
     * 脚本注释剔除
     */
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

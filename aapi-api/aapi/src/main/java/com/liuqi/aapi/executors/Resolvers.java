package com.liuqi.aapi.executors;

import com.alibaba.fastjson2.JSON;
import com.liuqi.aapi.bean.dto.ApiContext;
import com.liuqi.aapi.bean.dto.ParamConfig;
import com.liuqi.aapi.executors.resolver.JsonResolver;
import org.apache.commons.collections4.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 参数解析器
 *
 * @author LiuQi 16:59
 **/
public class Resolvers {
    private static final Map<String, ParamResolver> resolvers = new HashMap<>(16){{
        put("json", new JsonResolver());
    }};

    public static Object resolve(ApiContext context, ParamConfig paramConfig) {
        String type = paramConfig.getType();
        ParamResolver resolver = resolvers.get(type);
        if (null == resolver) {
            throw new RuntimeException("参数解析器不存在");
        }

        return resolver.resolve(context, paramConfig);
    }

    public static Object resolve(ApiContext context, List<ParamConfig> configs) {
        if (CollectionUtils.isEmpty(configs)) {
            return null;
        }

        // 如果结果配置中存在*的配置，则忽略其它配置
        Map<String, ParamConfig> configMap = configs.stream()
                .collect(Collectors.toMap(ParamConfig::getCode, s -> s));
        if (configMap.containsKey("*")) {
            ParamConfig paramConfig = configMap.get("*");
            return Resolvers.resolve(context, paramConfig);
        }

        // 其它情况，返回Map
        Map<String, Object> result = new HashMap<>(16);
        configMap.forEach((k, v) -> result.put(k, Resolvers.resolve(context, v)));

        return result;
    }
}

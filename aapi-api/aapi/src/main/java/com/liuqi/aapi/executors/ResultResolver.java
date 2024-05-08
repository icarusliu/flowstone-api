package com.liuqi.aapi.executors;

import com.alibaba.fastjson2.JSON;
import com.liuqi.aapi.bean.dto.ApiContext;
import com.liuqi.aapi.bean.dto.ParamConfig;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * 结果解析器
 *
 * @author LiuQi 16:06
 **/
public class ResultResolver {
    public static Object resolve(ApiContext context, String outputConfig) {
        if (StringUtils.isEmpty(outputConfig)) {
            return null;
        }

        List<ParamConfig> paramConfigs = JSON.parseArray(outputConfig, ParamConfig.class);
        return Resolvers.resolve(context, paramConfigs);
    }
}

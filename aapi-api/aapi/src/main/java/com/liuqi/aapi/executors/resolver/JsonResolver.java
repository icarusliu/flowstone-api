package com.liuqi.aapi.executors.resolver;

import com.alibaba.fastjson2.JSONObject;
import com.liuqi.aapi.bean.dto.ApiContext;
import com.liuqi.aapi.bean.dto.ApiParams;
import com.liuqi.aapi.bean.dto.ParamConfig;
import com.liuqi.aapi.executors.ParamResolver;

/**
 * JSON参数解析
 *
 * @author LiuQi 17:00
 **/
public class JsonResolver implements ParamResolver {
    @Override
    public Object resolve(ApiContext context, ParamConfig paramConfig) {
        ApiParams params = context.getParams();
        JSONObject paramsObject = JSONObject.from(params);
        String value = paramConfig.getValue();
        return paramsObject.getByPath(value);
    }
}

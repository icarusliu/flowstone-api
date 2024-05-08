package com.liuqi.aapi.executors;

import com.liuqi.aapi.bean.dto.ApiContext;
import com.liuqi.aapi.bean.dto.ApiParams;
import com.liuqi.aapi.bean.dto.ParamConfig;

/**
 * 参数解析
 *
 * @author LiuQi 16:59
 **/
public interface ParamResolver {
    Object resolve(ApiContext context, ParamConfig paramConfig);
}

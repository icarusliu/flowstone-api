package com.liuqi.aapi.executors;

import com.liuqi.aapi.bean.dto.ApiContext;
import com.liuqi.aapi.bean.dto.ApiNode;

/**
 * 节点执行器
 *
 * @author LiuQi 16:05
 **/
public interface NodeExecutor {
    Object execute(ApiContext context, ApiNode node);
}

package com.liuqi.dua.service;

import com.liuqi.dua.bean.req.ApiTestReq;

import java.util.Map;

/**
 * 统一数据服务
 */
public interface DuaService {
    /**
     * 执行对应的接口
     */
    Object execute(String id, Map<String, Object> params);

    /**
     * 接口测试
     * @param params 接口测试参数
     * @return 测试结果
     */
    Object test(String key, Map<String, Object> params);
}

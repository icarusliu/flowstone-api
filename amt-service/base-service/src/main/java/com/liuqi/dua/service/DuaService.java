package com.liuqi.dua.service;

import com.liuqi.dua.bean.dto.ApiDTO;
import com.liuqi.dua.bean.req.ApiTestReq;

import java.util.Map;

/**
 * 统一数据服务
 */
public interface DuaService {
    /**
     * 执行对应的接口
     */
    Object execute(ApiDTO api, Map<String, Object> params);

    /**
     * 接口测试
     * @param params 接口测试参数
     * @return 测试结果
     */
    Object test(ApiDTO api, Map<String, Object> params);
}

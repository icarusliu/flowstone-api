package com.liuqi.dua.service.impl;

import com.liuqi.dua.bean.dto.ApiDTO;
import com.liuqi.dua.bean.query.ApiQuery;
import com.liuqi.dua.executor.DagExecutor;
import com.liuqi.dua.service.ApiDraftService;
import com.liuqi.dua.service.ApiService;
import com.liuqi.dua.service.DuaService;
import jakarta.el.MethodNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
public class DuaServiceImpl implements DuaService {
    @Autowired
    private ApiService apiService;

    @Autowired
    private ApiDraftService apiDraftService;

    @Autowired
    private DagExecutor dagExecutor;

    /**
     * 执行对应的接口
     *
     * @param params 请求参数
     */
    @Override
    public Object execute(ApiDTO api, Map<String, Object> params) {
        return dagExecutor.execute(api, params, false);
    }

    /**
     * 接口测试
     *
     * @param params 接口测试参数
     * @return 测试结果
     */
    @Override
    public Object test(ApiDTO apiDTO, Map<String, Object> params) {
        return dagExecutor.execute(apiDTO, params, true);
    }
}

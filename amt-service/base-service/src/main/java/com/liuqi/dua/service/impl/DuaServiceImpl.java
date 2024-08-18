package com.liuqi.dua.service.impl;

import com.liuqi.dua.bean.dto.ApiDTO;
import com.liuqi.dua.bean.dto.ApiDraftDTO;
import com.liuqi.dua.bean.query.ApiDraftQuery;
import com.liuqi.dua.bean.query.ApiQuery;
import com.liuqi.dua.service.ApiDraftService;
import com.liuqi.dua.service.ApiService;
import com.liuqi.dua.service.DuaService;
import com.liuqi.dua.executor.DagExecutor;
import jakarta.el.MethodNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
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
    public Object execute(String key, Map<String, Object> params) {
        ApiQuery q = new ApiQuery();
        q.setKey(key);
        ApiDTO api = apiService.findOne(q).orElseThrow(MethodNotFoundException::new);

        return dagExecutor.execute(api, params, false);
    }

    /**
     * 接口测试
     *
     * @param key 接口关键字，id或者路径
     * @param params 接口测试参数
     * @return 测试结果
     */
    @Override
    public Object test(String key, Map<String, Object> params) {
        ApiDraftQuery query = new ApiDraftQuery();
        query.setKey(key);
        ApiDraftDTO api = apiDraftService.findOne(query)
                .orElseThrow(MethodNotFoundException::new);

        ApiDTO apiDTO = new ApiDTO();
        BeanUtils.copyProperties(api, apiDTO);

        return dagExecutor.execute(apiDTO, params, true);
    }
}

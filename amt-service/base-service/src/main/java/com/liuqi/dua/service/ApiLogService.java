package com.liuqi.dua.service;

import com.liuqi.base.bean.dto.ClientDTO;
import com.liuqi.common.base.service.BaseService;
import com.liuqi.dua.bean.dto.ApiDTO;
import com.liuqi.dua.bean.dto.ApiLogDTO;
import com.liuqi.dua.bean.query.ApiLogQuery;

import java.util.List;
import java.util.Map;

/**
 * 接口执行日志服务接口
 *
 * @author Coder Generator 2024-08-14 14:42:30
 **/
public interface ApiLogService extends BaseService<ApiLogDTO, ApiLogQuery> {
    /**
     * 添加异常日志
     *
     * @param api      接口信息
     * @param client   客户端信息
     * @param params   请求参数
     * @param errorMsg 异常信息
     */
    void addErrorLog(ApiDTO api, ClientDTO client, Map<String, Object> params, String errorMsg);

    /**
     * 添加异常日志
     *
     * @param api           接口信息
     * @param requestParams 请求参数
     * @param errorMsg      异常信息
     * @param exceptions    异常详细信息
     */
    void addErrorLog(ApiDTO api, ClientDTO client, Map<String, Object> requestParams, String errorMsg, List<Exception> exceptions);

    /**
     * 接口执行成功日志
     *
     * @param api           接口信息
     * @param client        客户端信息
     * @param requestParams 请求参数
     * @param result        执行结果
     * @param spentTime     执行时间
     */
    void addSuccessLog(ApiDTO api, ClientDTO client, Map<String, Object> requestParams, Object result, long spentTime);
}
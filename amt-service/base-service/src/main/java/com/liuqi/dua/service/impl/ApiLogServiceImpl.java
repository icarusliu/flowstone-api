package com.liuqi.dua.service.impl;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.liuqi.common.base.service.AbstractBaseService;
import com.liuqi.common.utils.ExceptionUtils;
import com.liuqi.common.bean.UserContextHolder;
import com.liuqi.dua.bean.dto.ApiDTO;
import com.liuqi.dua.bean.dto.ApiLogDTO;
import com.liuqi.dua.bean.query.ApiLogQuery;
import com.liuqi.dua.domain.entity.ApiLogEntity;
import com.liuqi.dua.domain.mapper.ApiLogMapper;
import com.liuqi.dua.service.ApiLogService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 接口执行日志服务实现
 *
 * @author Coder Generator 2024-08-14 14:42:30
 **/
@Service
public class ApiLogServiceImpl extends AbstractBaseService<ApiLogEntity, ApiLogDTO, ApiLogMapper, ApiLogQuery> implements ApiLogService {
    @Override
    public ApiLogDTO toDTO(ApiLogEntity entity) {
        ApiLogDTO dto = new ApiLogDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    @Override
    public ApiLogEntity toEntity(ApiLogDTO dto) {
        ApiLogEntity entity = new ApiLogEntity();
        BeanUtils.copyProperties(dto, entity);
        return entity;
    }

    @Override
    protected QueryWrapper<ApiLogEntity> queryToWrapper(ApiLogQuery query) {
        return this.createQueryWrapper()
                .eq(StringUtils.isNotBlank(query.getId()), "id", query.getId())
                .in(null != query.getIds(), "id", query.getIds())
                .eq("deleted", false)
                .eq(null != query.getStatus(), "status", query.getStatus())
                .and(StringUtils.isNotBlank(query.getKey()), qr -> qr.like("api_path", query.getKey())
                        .or(q -> q.like("api_name", query.getKey())))
                .orderByDesc("create_time");
    }

    /**
     * 添加异常日志
     *
     * @param api      接口信息
     * @param params   请求参数
     * @param errorMsg 异常信息
     */
    @Override
    public void addErrorLog(ApiDTO api, Map<String, Object> params, String errorMsg) {
        ApiLogDTO log = apiToLog(api, params);
        log.setStatus(1);
        log.setErrorMsg(errorMsg);
        this.insertAsync(log, UserContextHolder.getUserId().orElse("guest"));
    }

    private static ApiLogDTO apiToLog(ApiDTO api, Map<String, Object> params) {
        ApiLogDTO log = new ApiLogDTO();
        log.setApiId(api.getId());
        log.setApiPath(api.getPath());
        log.setApiName(api.getName());
        log.setParams(JSON.toJSONString(params));
        return log;
    }

    @Async
    private void insertAsync(ApiLogDTO log, String userId) {
        log.setCreateUser(userId);
        log.setUpdateUser(userId);

        String result = log.getResult();
        if (StringUtils.isNotBlank(result) && result.length() > 255) {
            result = result.substring(0, 250) + "...";
            log.setResult(result);
        }

        this.insert(log);
    }

    /**
     * 添加异常日志
     *
     * @param api           接口信息
     * @param requestParams 请求参数
     * @param errorMsg      异常信息
     * @param exceptions    异常详细信息
     */
    @Override
    public void addErrorLog(ApiDTO api, Map<String, Object> requestParams, String errorMsg, List<Exception> exceptions) {
        ApiLogDTO log = apiToLog(api, requestParams);
        log.setStatus(1);
        log.setErrorMsg(errorMsg);

        // 组装异常信息
        String detail = ExceptionUtils.asString(exceptions);
        log.setResult(detail);

        this.insertAsync(log, UserContextHolder.getUserId().orElse("guest"));
    }

    /**
     * 接口执行成功日志
     *
     * @param api           接口信息
     * @param requestParams 请求参数
     * @param result        执行结果
     * @param spentTime 执行时间
     */
    @Override
    public void addSuccessLog(ApiDTO api, Map<String, Object> requestParams, Object result, long spentTime) {
        ApiLogDTO log = apiToLog(api, requestParams);
        log.setStatus(0);
        log.setResult(JSON.toJSONString(result));
        log.setSpentTime((int) spentTime);
        this.insertAsync(log, UserContextHolder.getUserId().orElse("guest"));
    }
}
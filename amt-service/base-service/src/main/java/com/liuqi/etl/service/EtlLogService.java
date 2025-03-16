package com.liuqi.etl.service;

import com.liuqi.common.base.service.BaseService;
import com.liuqi.etl.bean.dto.EtlLogDTO;
import com.liuqi.etl.bean.query.EtlLogQuery;

import java.time.LocalDate;

/**
 * ETL执行日志服务接口 
 * @author Coder Generator 2025-03-10 16:37:56 
 **/
public interface EtlLogService extends BaseService<EtlLogDTO, EtlLogQuery> {
    /**
     * 日志清理
     * @param localDate 清理指定日期前的日志
     */
    void clearLogsBefore(LocalDate localDate);
}
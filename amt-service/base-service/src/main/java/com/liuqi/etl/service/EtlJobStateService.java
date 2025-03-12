package com.liuqi.etl.service;

import com.liuqi.common.base.service.BaseService;
import com.liuqi.etl.bean.dto.EtlJobStateDTO;
import com.liuqi.etl.bean.query.EtlJobStateQuery;

import java.time.LocalDate;
import java.util.List;

/**
 * 执行完成任务记录服务接口 
 * @author Coder Generator 2025-03-10 17:53:08 
 **/
public interface EtlJobStateService extends BaseService<EtlJobStateDTO, EtlJobStateQuery> {
    /**
     * 根据任务id列表查询任务执行情况
     * @param jobIds 任务id列表
     * @return 执行情况
     */
    default List<EtlJobStateDTO> findByJobIds(List<String> jobIds, LocalDate dataDate) {
        EtlJobStateQuery query = new EtlJobStateQuery();
        query.setJobIds(jobIds);
        query.setExecuteDate(dataDate);
        return this.query(query);
    }
}
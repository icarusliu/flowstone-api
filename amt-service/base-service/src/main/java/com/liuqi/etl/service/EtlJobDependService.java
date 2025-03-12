package com.liuqi.etl.service;

import com.liuqi.common.base.service.BaseService;
import com.liuqi.etl.bean.dto.EtlJobDependDTO;
import com.liuqi.etl.bean.query.EtlJobDependQuery;

import java.util.List;

/**
 * 任务依赖服务接口 
 * @author Coder Generator 2025-03-10 17:05:53 
 **/
public interface EtlJobDependService extends BaseService<EtlJobDependDTO, EtlJobDependQuery> {
    /**
     * 根据父任务查找子任务列表
     * @param parentId 父任务id
     * @return 查找结果
     */
    default List<EtlJobDependDTO> findByParent(String parentId) {
        EtlJobDependQuery query = new EtlJobDependQuery();
        query.setParentJobId(parentId);
        return this.query(query);
    }

    /**
     * 根据任务列表批量查询
     * @param jobIds 任务id列表
     * @return 查询结果
     */
    default List<EtlJobDependDTO> findByJobs(List<String> jobIds) {
        EtlJobDependQuery query = new EtlJobDependQuery();
        query.setJobIds(jobIds);
        return this.query(query);
    }

    /**
     * 更新任务依赖
     */
    void update(String jobId, List<String> dependJobIds);
}
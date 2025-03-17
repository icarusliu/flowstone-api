package com.liuqi.etl.service;

import com.liuqi.common.base.service.BaseService;
import com.liuqi.etl.bean.dto.EtlJobDTO;
import com.liuqi.etl.bean.query.EtlJobQuery;
import com.liuqi.etl.bean.resp.BloodTree;

import java.util.List;

/**
 * ETL任务服务接口 
 * @author Coder Generator 2025-03-10 16:36:10 
 **/
public interface EtlJobService extends BaseService<EtlJobDTO, EtlJobQuery> {
    /**
     * 自动解析任务依赖
     * @param jobId 任务id
     * @return 依赖的直接任务清单
     */
    List<EtlJobDTO> parseDepends(String jobId);

    /**
     * 发布任务
     * @param jobId 任务id
     */
    void publish(String jobId);

    /**
     * 任务下线
     * @param jobId 任务id
     */
    void offline(String jobId);

    /**
     * 血缘分析获取结果树
     * @param id 任务id
     */
    List<BloodTree> getBloodRelation(String id);
}
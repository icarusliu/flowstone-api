package com.liuqi.etl.bean.query;

import com.liuqi.common.base.bean.query.BaseQuery;
import lombok.Data;

import java.util.List;

/**
 * 任务依赖查询对象 
 * @author Coder Generator 2025-03-10 16:48:20 
 **/
@Data
public class EtlJobDependQuery extends BaseQuery {
    private String parentJobId;
    private List<String> jobIds;
    private String jobId;
}
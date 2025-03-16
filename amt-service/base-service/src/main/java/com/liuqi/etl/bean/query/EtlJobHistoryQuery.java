package com.liuqi.etl.bean.query;

import com.liuqi.common.base.bean.query.BaseQuery;
import lombok.Data;

/**
 * ETL任务历史记录查询对象 
 * @author Coder Generator 2025-03-16 17:21:59 
 **/
@Data
public class EtlJobHistoryQuery extends BaseQuery {
    private String jobId;
}
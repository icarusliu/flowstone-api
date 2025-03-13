package com.liuqi.etl.bean.query;

import com.liuqi.common.base.bean.query.BaseQuery;
import lombok.Data;

/**
 * ETL已发布任务查询对象 
 * @author Coder Generator 2025-03-11 21:48:25 
 **/
@Data
public class EtlJobPublishedQuery extends BaseQuery {
    private Boolean autoTrigger;
    private Boolean cronNotNull;
    private String type;
}
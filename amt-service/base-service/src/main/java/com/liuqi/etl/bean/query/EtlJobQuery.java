package com.liuqi.etl.bean.query;

import com.liuqi.common.base.bean.query.BaseQuery;
import lombok.Data;

import java.util.List;

/**
 * ETL任务查询对象 
 * @author Coder Generator 2025-03-10 16:36:10 
 **/
@Data
public class EtlJobQuery extends BaseQuery {
    private Boolean autoTrigger;
    private String typeId;
    private String key;
    private List<String> updatedTables;
    private String code;
    private String name;
}
package com.liuqi.etl.bean.query;

import com.liuqi.common.base.bean.query.BaseQuery;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/**
 * 任务状态记录查询对象
 * @author Coder Generator 2025-03-10 17:53:08 
 **/
@Data
public class EtlJobStateQuery extends BaseQuery {
    private List<String> jobIds;
    private LocalDate executeDate;
    private Integer state;
    private List<Integer> states;
}
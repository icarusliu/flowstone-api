package com.liuqi.etl.bean.query;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.liuqi.common.base.bean.query.BaseQuery;
import lombok.Data;

import java.time.LocalDate;

/**
 * ETL执行日志查询对象 
 * @author Coder Generator 2025-03-10 16:37:56 
 **/
@Data
public class EtlLogQuery extends BaseQuery {
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate beginDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    private String key;

    private Integer status;
}
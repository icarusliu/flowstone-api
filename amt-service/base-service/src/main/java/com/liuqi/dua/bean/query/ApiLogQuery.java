package com.liuqi.dua.bean.query;

import com.liuqi.common.base.bean.query.BaseQuery;
import lombok.Data;

/**
 * 接口执行日志查询对象
 *
 * @author Coder Generator 2024-08-14 14:42:30
 **/
@Data
public class ApiLogQuery extends BaseQuery {
    private Integer status;
    private String key;
    private String clientId;
}
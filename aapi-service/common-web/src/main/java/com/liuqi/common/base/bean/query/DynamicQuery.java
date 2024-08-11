package com.liuqi.common.base.bean.query;

import lombok.Data;

import java.util.List;

/**
 * 动态查询
 *
 * @author 不空军 15:34
 **/
@Data
public class DynamicQuery {
    private Integer pageNo;

    private Integer pageSize;

    private List<Filter> filters;
}

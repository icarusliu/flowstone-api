package com.liuqi.common.base.bean.query;

import lombok.Data;

import java.util.List;

/**
 * 过滤器
 *
 * @author 不空军 15:08
 **/
@Data
public class Filter {
    /**
     * 操作符
     */
    private FilterOp op;

    /**
     * 字段
     */
    private String key;

    /**
     * 字段值
     */
    private Object value;

    /**
     * 默认值，用于value里面存储参数名称的情况
     */
    private Object defValue;

    /**
     * 字段值1
     */
    private Object value1;

    /**
     * 子查询条件
     */
    private List<Filter> filters;
}

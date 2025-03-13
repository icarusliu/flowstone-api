package com.liuqi.common.base.bean.query;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 动态查询
 *
 * @author 不空军 15:34
 **/
@Data
public class DynamicQuery {
    public static DynamicQuery create() {
        return new DynamicQuery();
    }

    private Integer pageNo;

    private Integer pageSize;

    private List<Filter> filters = new ArrayList<>(16);

    public DynamicQuery addFilter(Filter filter) {
        filters.add(filter);
        return this;
    }

    public DynamicQuery addFilter(String key, String value, FilterOp op) {
        Filter filter = new Filter();
        filter.setOp(op);
        filter.setKey(key);
        filter.setValue(value);
        return this.addFilter(filter);
    }

    public DynamicQuery eq(String key, Object value) {
        if (null == value || "".equals(value)) {
            return this;
        }

        Filter filter = new Filter();
        filter.setOp(FilterOp.EQ);
        filter.setKey(key);
        filter.setValue(value);
        return this.addFilter(filter);
    }
}

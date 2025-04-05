package com.liuqi.common.base.bean.query;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.liuqi.common.base.service.BaseService;
import lombok.Data;

import java.util.Collection;
import java.util.List;

/**
 * 动态查询
 *
 * @author 不空军 15:34
 **/
@Data
public class DynamicQueryBuilder<D> {
    public static <D> DynamicQueryBuilder<D> create(BaseService<D, ?> baseService) {
        return new DynamicQueryBuilder<D>(baseService);
    }

    private Long pageNo;

    private Long pageSize;

    private BaseService<D, ?> baseService;
    private DynamicQuery query = new DynamicQuery();

    public DynamicQueryBuilder(BaseService<D, ?> baseService) {
        this.baseService = baseService;
    }

    public DynamicQueryBuilder<D> addFilter(Filter filter) {
        query.addFilter(filter);
        return this;
    }

    public DynamicQueryBuilder<D> addFilter(String key, String value, FilterOp op) {
        query.addFilter(key, value, op);
        return this;
    }

    public DynamicQueryBuilder<D> eq(String key, Object value) {
        query.eq(key, value);
        return this;
    }

    public DynamicQueryBuilder<D> neq(String key, Object value) {
        query.neq(key, value);
        return this;
    }

    public DynamicQueryBuilder<D> in(String key, Collection<?> collection) {
        query.in(key, collection);
        return this;
    }

    public List<D> query() {
        return baseService.dynamicQuery(query);
    }

    public IPage<D> pageQuery() {
        return baseService.dynamicPageQuery(query);
    }
}

package com.liuqi.common.base.bean.query;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 更新构建器
 *
 * @author LiuQi 2025/5/6-19:48
 * @version V1.0
 **/
public class QueryBuilder<D, E> {
    private final QueryWrapper<E> queryWrapper;
    private final Function<QueryWrapper<E>, List<D>> queryFunc;
    private final Function<QueryBuilder<D, E>, IPage<D>> pageQueryFunc;
    private final Function<QueryWrapper<E>, Long> countFunc;
    private Long pageNo = 1L;
    private Long pageSize = 10L;

    public QueryBuilder(Function<QueryWrapper<E>, List<D>> queryFunc,
                        Function<QueryBuilder<D, E>, IPage<D>> pageQueryFunc,
                        Function<QueryWrapper<E>, Long> countFunc) {
        this.queryFunc = queryFunc;
        this.pageQueryFunc = pageQueryFunc;
        this.countFunc = countFunc;
        queryWrapper = new QueryWrapper<>();
    }

    public static <D, E> QueryBuilder<D, E> create(Function<QueryWrapper<E>, List<D>> queryFunc,
                                                   Function<QueryBuilder<D, E>, IPage<D>> pageQueryFunc,
                                                   Function<QueryWrapper<E>, Long> countFunc) {
        return new QueryBuilder<>(queryFunc, pageQueryFunc, countFunc);
    }

    public QueryBuilder<D, E> eq(String key, Object value) {
        if (null == value) {
            return this;
        }
        queryWrapper.eq(key, value);
        return this;
    }

    public QueryBuilder<D, E> neq(String key, Object value) {
        if (null == value) {
            return this;
        }
        queryWrapper.ne(key, value);
        return this;
    }

    public QueryBuilder<D, E> in(String key, Collection<?> list) {
        if (CollectionUtils.isEmpty(list)) {
            return this;
        }
        queryWrapper.in(key, list);
        return this;
    }

    public QueryBuilder<D, E> notIn(String key, Collection<?> list) {
        if (CollectionUtils.isEmpty(list)) {
            return this;
        }
        queryWrapper.notIn(key, list);
        return this;
    }

    public QueryBuilder<D, E> like(String key, String val) {
        if (StringUtils.isBlank(val)) {
            return this;
        }
        queryWrapper.like(key, val);
        return this;
    }

    public QueryBuilder<D, E> notLike(String key, String val) {
        if (StringUtils.isBlank(val)) {
            return this;
        }
        queryWrapper.notLike(key, val);
        return this;
    }

    public QueryBuilder<D, E> lt(String key, Object val) {
        if (null == val) {
            return this;
        }
        queryWrapper.lt(key, val);
        return this;
    }

    public QueryBuilder<D, E> le(String key, Object val) {
        if (null == val) {
            return this;
        }
        queryWrapper.le(key, val);
        return this;
    }

    public QueryBuilder<D, E> gt(String key, Object val) {
        if (null == val) {
            return this;
        }
        queryWrapper.gt(key, val);
        return this;
    }

    public QueryBuilder<D, E> ge(String key, Object val) {
        if (null == val) {
            return this;
        }
        queryWrapper.ge(key, val);
        return this;
    }

    public QueryBuilder<D, E> ne(String key, Object val) {
        if (null == val) {
            return this;
        }
        queryWrapper.ne(key, val);
        return this;
    }

    public QueryBuilder<D, E> isNull(String key) {
        queryWrapper.isNull(key);
        return this;
    }

    public QueryBuilder<D, E> isNotNull(String key) {
        queryWrapper.isNotNull(key);
        return this;
    }

    public QueryBuilder<D, E> notNull(String key) {
        queryWrapper.isNotNull(key);
        return this;
    }

    /**
     * 增加or条件
     * @param consumer 消费函数
     * @return 当前对象
     */
    public QueryBuilder<D, E> or(Consumer<QueryWrapper<E>> consumer) {
        this.queryWrapper.or(consumer);
        return this;
    }

    public QueryBuilder<D, E> setPageNo(Long pageNo) {
        this.pageNo = pageNo;
        return this;
    }

    public QueryBuilder<D, E> setPageSize(Long pageSize) {
        this.pageSize = pageSize;
        return this;
    }

    public QueryBuilder<D, E> orderByDesc(String column) {
        this.queryWrapper.orderByDesc(column);
        return this;
    }

    public QueryBuilder<D, E> orderByAsc(String column) {
        this.queryWrapper.orderByAsc(column);
        return this;
    }

    public QueryBuilder<D, E> orderBy(String column, boolean isAsc) {
        if (isAsc) {
            this.queryWrapper.orderByAsc(column);
        } else {
            this.queryWrapper.orderByDesc(column);
        }
        return this;
    }

    public List<D> query() {
        if (null != pageNo && null != pageSize) {
            long start = (pageNo - 1) * pageSize;
            this.queryWrapper.last("limit " + start + "," + pageSize);
        }
        return queryFunc.apply(this.queryWrapper);
    }

    /**
     * 执行查询将将结果转换成map
     * @param keyFunc key的转换函数
     * @return 转换后的结果
     * @param <K> key类型
     */
    public <K> Map<K, D> query(Function <D, K> keyFunc) {
        return this.query()
                .stream()
                .collect(Collectors.toMap(keyFunc, d -> d));
    }

    /**
     * 执行查询并将结果转换成map
     * @param keyFunc key的转换函数
     * @param valueFunc value的转换函数
     * @return 转换结果
     * @param <K> key类型
     * @param <V> value类型
     */
    public <K, V> Map<K, V> query(Function<D, K> keyFunc, Function<D, V> valueFunc) {
        return this.query()
                .stream()
                .collect(Collectors.toMap(keyFunc, valueFunc));
    }

    public IPage<D> pageQuery() {
        return pageQueryFunc.apply(this);
    }

    public Long count() {
        return countFunc.apply(this.queryWrapper);
    }

    public QueryWrapper<E> getQueryWrapper() {
        return queryWrapper;
    }

    public Long getPageNo() {
        return pageNo;
    }

    public Long getPageSize() {
        return pageSize;
    }
}

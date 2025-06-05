package com.liuqi.common.base.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.liuqi.common.base.bean.query.*;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public interface BaseService<D, Q extends BaseQuery> {
    D insert(D dto);

    List<D> insert(List<D> dtos);

    void update(D dto);

    UpdateBuilder<?> updateBuilder();

    List<D> query(Q q);

    long count(Q q);

    IPage<D> pageQuery(Q q);

    void delete(String id);

    void deletePhysical(String id);

    void delete(Collection<String> ids);

    void deletePhysical(Collection<String> ids);

    /**
     * 动态查询
     */
    QueryBuilder<D, ?> queryBuilder();

    /**
     * 查询单个数据
     *
     * @param q 查询对象
     * @return 查询到的记录
     */
    default Optional<D> findOne(Q q) {
        return this.query(q).stream().findAny();
    }

    /**
     * 根据id查找记录
     *
     * @param id id
     * @return id对应的记录
     */
    default Optional<D> findById(String id) {
        return this.queryBuilder()
                .eq("id", id)
                .query()
                .stream()
                .findAny();
    }

    /**
     * 根据id批量查询记录
     *
     * @param ids id列表
     * @return id列表对应的记录
     */
    default List<D> findByIds(List<String> ids) {
        return this.queryBuilder()
                .in("id", ids)
                .query();
    }

    /**
     * 查找所有记录
     */
    default List<D> findAll() {
        return this.queryBuilder()
                .query();
    }

    /**
     * 查找记录并将结果进行转换
     * @param func 转换函数
     * @return 转换后的数据
     * @param <T> 转换后的对象类型
     */
    default <T> List<T> findAllAndConvert(Function<D, T> func) {
        return this.findAll()
                .stream()
                .map(func)
                .toList();
    }

    /**
     * 查找所有记录并转换成map返回
     * @param func key转换函数
     * @return 转换后的map
     */
    default <K> Map<K, D> findAll(Function<D, K> func) {
        return this.findAll()
                .stream()
                .collect(Collectors.toMap(func, d -> d));
    }

    /**
     * 查找所有记录并转换成map返回
     * @param func key转换函数
     * @return 转换后的map
     */
    default <K, V> Map<K, V> findAll(Function<D, K> func, Function<D, V> valueFunc) {
        return this.findAll()
                .stream()
                .collect(Collectors.toMap(func, valueFunc));
    }

    /**
     * 查找所有记录，根据指定字段排序
     *
     * @param orderByColumn 字段
     * @param isAsc         是否升级
     * @return 结果
     */
    default List<D> findAll(String orderByColumn, boolean isAsc) {
        return this.queryBuilder()
                .orderBy(orderByColumn, isAsc)
                .query();
    }

    /**
     * 查找所有记录，按指定字段升级排序查询
     *
     * @param orderByColumn 排序字段
     * @return 结果
     */
    default List<D> findAll(String orderByColumn) {
        return this.queryBuilder()
                .orderByAsc(orderByColumn)
                .query();
    }
}

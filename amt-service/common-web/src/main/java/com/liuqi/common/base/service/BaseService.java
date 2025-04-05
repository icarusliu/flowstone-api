package com.liuqi.common.base.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.liuqi.common.base.bean.query.DynamicQuery;
import com.liuqi.common.base.bean.query.BaseQuery;
import com.liuqi.common.base.bean.query.DynamicQueryBuilder;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface BaseService<D, Q extends BaseQuery> {
    D insert(D dto);

    List<D> insert(List<D> dtos);

    void update(D dto);

    List<D> query(Q q);

    long count(Q q);

    IPage<D> pageQuery(Q q);

    void delete(String id);

    void deletePhysical(String id);

    void delete(Collection<String> ids);

    void deletePhysical(Collection<String> ids);

    /**
     * 动态查询
     *
     * @param query 查询对象
     * @return 查询结果
     */
    IPage<D> dynamicPageQuery(DynamicQuery query);

    /**
     * 动态查询
     *
     * @param query 查询对象
     * @return 查询结果
     */
    List<D> dynamicQuery(DynamicQuery query);

    /**
     * 动态查询
     */
    DynamicQueryBuilder<D> dynamicQuery();

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
    Optional<D> findById(String id);

    /**
     * 根据id批量查询记录
     *
     * @param ids id列表
     * @return id列表对应的记录
     */
    List<D> findByIds(List<String> ids);

    /**
     * 查找所有记录
     */
    List<D> findAll();

    /**
     * 查找所有记录，根据指定字段排序
     *
     * @param orderByColumn 字段
     * @param isAsc         是否升级
     * @return 结果
     */
    List<D> findAll(String orderByColumn, boolean isAsc);

    /**
     * 查找所有记录，按指定字段升级
     *
     * @param orderByColumn 排序字段
     * @return 结果
     */
    default List<D> findAll(String orderByColumn) {
        return this.findAll(orderByColumn, true);
    }
}

package com.liuqi.common.base.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.liuqi.common.base.bean.dto.BaseDTO;
import com.liuqi.common.base.bean.query.DynamicQuery;
import com.liuqi.common.base.domain.entity.BaseEntity;
import com.liuqi.common.base.bean.query.BaseQuery;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public interface BaseService<D extends BaseDTO, Q extends BaseQuery> {
    D insert(D dto);

    void insert(List<D> dtos);

    void update(D dto);

    List<D> query(Q q);

    Optional<D> findById(String id);

    List<D> findByIds(List<String> ids);

    long count(Q q);

    IPage<D> pageQuery(Q q);

    void delete(String id);

    void deletePhysical(String id);

    void delete(Collection<String> ids);

    void deletePhysical(Collection<String> ids);

    /**
     * 动态查询
     * @param query 查询对象
     * @return 查询结果
     */
    IPage<D> dynamicQuery(DynamicQuery query);
}

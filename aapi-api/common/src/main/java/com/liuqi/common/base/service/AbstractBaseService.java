package com.liuqi.common.base.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liuqi.common.base.bean.dto.BaseDTO;
import com.liuqi.common.base.bean.query.BaseQuery;
import com.liuqi.common.base.bean.query.DynamicQuery;
import com.liuqi.common.base.bean.query.Filter;
import com.liuqi.common.base.bean.query.FilterOp;
import com.liuqi.common.base.domain.entity.BaseEntity;
import com.liuqi.common.base.domain.mapper.BaseMapper;
import com.liuqi.common.exception.AppException;
import com.liuqi.common.exception.CommErrorCodes;
import com.liuqi.common.utils.UserContextHolder;
import liquibase.util.StringUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 抽象实体服务，完成基础数据库操作的封装
 *
 * @param <E> Entity实体对象
 * @param <D> DTO数据对象
 * @param <M> 数据库操作Mapper
 * @param <Q> Query查询对象
 */
public abstract class AbstractBaseService<E extends BaseEntity, D extends BaseDTO, M extends BaseMapper<E>, Q extends BaseQuery>
        extends ServiceImpl<M, E> implements BaseService<D, Q> {
    /**
     * 实体插入操作
     *
     * @param dto 插入的数据对象
     * @return 插入完成后的对象，包含有生成的id
     */
    @Override
    @Transactional
    public D insert(D dto) {
        if (!this.processBeforeInsert(dto)) {
            return dto;
        }

        this.setCreateFields(dto);

        E entity = this.toEntity(dto);
        this.save(entity);
        return this.toDTO(entity);
    }

    /**
     * 设置基础字段值
     *
     * @param dto 数据对象
     */
    protected void setCreateFields(D dto) {
        UserContextHolder.get()
                .ifPresent(user -> {
                    dto.setCreator(user.getDisplayName());
                    dto.setCreateTime(LocalDateTime.now());
                    dto.setDeleted(false);
                    dto.setTenantId(user.getTenantId());
                });
    }

    @Override
    public void insert(List<D> dtos) {
        // 框架层实现，一条条处理，如果需要批量处理并讲究效率，请使用saveBatch
        if (CollectionUtils.isEmpty(dtos)) {
            return;
        }

        dtos.forEach(this::insert);
    }

    /**
     * 插入前处理
     * 可以执行相关参数校验，校验不通过时抛出异常
     *
     * @return false时不进行插入，true时进行插入
     */
    protected boolean processBeforeInsert(D dto) {
        return true;
    }

    /**
     * 更新记录
     *
     * @param dto 待更新记录内容，id不能为空
     */
    @Override
    @Transactional
    public void update(D dto) {
        if (StringUtils.isEmpty(dto.getId())) {
            throw AppException.of(CommErrorCodes.FIELD_NULL, "id");
        }

        if (!this.processBeforeUpdate(dto)) {
            return;
        }

        this.setUpdateFields(dto);

        E entity = this.toEntity(dto);
        this.save(entity);
    }

    /**
     * 设置更新字段值
     *
     * @param dto 数据对象
     */
    protected void setUpdateFields(D dto) {
        UserContextHolder.get()
                .ifPresent(user -> {
                    dto.setUpdater(user.getDisplayName());
                    dto.setUpdateTime(LocalDateTime.now());
                });
    }

    /**
     * 更新前处理
     *
     * @return false时不再进行更新，true时进行更新
     */
    protected boolean processBeforeUpdate(D dto) {
        return true;
    }

    /**
     * 执行查询操作
     *
     * @param q 查询对象
     * @return 查询到的列表数据
     */
    @Override
    public List<D> query(Q q) {
        QueryWrapper<E> queryWrapper = this.queryToWrapper(q);
        return this.toDTO(this.list(queryWrapper));
    }

    @Override
    public Optional<D> findById(String id) {
        return this.list(this.createQueryWrapper().eq("id", id)).stream().findFirst()
                .map(this::toDTO);
    }

    @Override
    public List<D> findByIds(List<String> ids) {
        return this.list(this.createQueryWrapper().in("id", ids))
                .stream().map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * 查找所有记录
     *
     * @return 所有记录
     */
    protected List<D> findAll() {
        return this.toDTO(this.list(this.createQueryWrapper()));
    }

    /**
     * 查询单个数据
     *
     * @param q 查询对象
     * @return 查询到的记录
     */
    protected Optional<D> findOne(Q q) {
        return this.query(q).stream().findAny();
    }

    /**
     * 查询对象转QueryWrapper
     *
     * @param q 查询对象
     * @return 包含有对应条件的QueryWrapper
     */
    protected abstract QueryWrapper<E> queryToWrapper(Q q);

    /**
     * 创建QueryWrapper
     *
     * @return 创建好的QueryWrapper
     */
    protected QueryWrapper<E> createQueryWrapper() {
        QueryWrapper<E> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("deleted", false);

        return queryWrapper;
    }

    /**
     * 创建更新Wrapper
     *
     * @return 创建好的更新Wrapper
     */
    protected UpdateWrapper<E> createUpdateWrapper() {
        return new UpdateWrapper<>();
    }

    /**
     * 分页查询
     *
     * @param q 查询对象
     * @return 查询结果
     */
    @Override
    @Transactional
    public IPage<D> pageQuery(Q q) {
        IPage<E> pageReq = new Page<>();
        pageReq.setCurrent(q.getPageNo());
        pageReq.setSize(q.getPageSize());
        IPage<E> result = this.page(pageReq, this.queryToWrapper(q));

        IPage<D> finalResult = new Page<>();
        finalResult.setTotal(result.getTotal());
        finalResult.setSize(q.getPageSize());
        finalResult.setCurrent(q.getPageNo());
        finalResult.setPages(result.getPages());
        finalResult.setRecords(this.toDTO(result.getRecords()));

        return finalResult;
    }

    /**
     * 删除前处理
     *
     * @return true时进行删除，false时不进行删除
     */
    protected boolean processBeforeDelete(Collection<String> ids) {
        return true;
    }

    /**
     * 删除后处理
     */
    protected void processAfterDelete(Collection<String> ids) {
    }

    /**
     * 逻辑删除
     *
     * @param id 待删除记录id
     */
    @Override
    @Transactional
    public void delete(String id) {
        if (!this.processBeforeDelete(Collections.singleton(id))) {
            return;
        }

        // 逻辑删除
        UpdateWrapper<E> updateWrapper = this.createUpdateWrapper();
        updateWrapper.eq("id", id)
                .set("deleted", false);
        this.update(updateWrapper);

        this.processAfterDelete(Collections.singleton(id));
    }

    /**
     * 批量逻辑删除
     *
     * @param ids 待删除记录id列表
     */
    @Override
    @Transactional
    public void delete(Collection<String> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return;
        }

        if (!this.processBeforeDelete(ids)) {
            return;
        }

        this.update(this.createUpdateWrapper().in("id", ids).set("deleted", false));

        this.processAfterDelete(ids);
    }

    /**
     * 物理删除
     *
     * @param id 记录id
     */
    @Override
    @Transactional
    public void deletePhysical(String id) {
        if (!this.processBeforeDelete(Collections.singleton(id))) {
            return;
        }

        this.removeById(id);

        this.processAfterDelete(Collections.singleton(id));
    }

    /**
     * 物理删除
     *
     * @param ids 记录id列表
     */
    @Override
    @Transactional
    public void deletePhysical(Collection<String> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return;
        }

        if (!this.processBeforeDelete(ids)) {
            return;
        }

        this.removeByIds(ids);
        this.processAfterDelete(ids);
    }

    /**
     * 动态查询
     *
     * @param query 查询对象
     * @return 查询结果
     */
    @Override
    public IPage<D> dynamicQuery(DynamicQuery query) {
        QueryWrapper<E> queryWrapper = this.createQueryWrapper();
        List<Filter> filters = query.getFilters();
        if (!CollectionUtils.isEmpty(filters)) {
            filters.forEach(filter -> this.processFilter(filter, queryWrapper));
        }

        int pageNo = Optional.ofNullable(query.getPageNo()).orElse(1);
        int pageSize = Optional.ofNullable(query.getPageSize()).orElse(10000);
        IPage<E> pageReq = new Page<>();
        pageReq.setCurrent(pageNo);
        pageReq.setSize(pageSize);
        IPage<E> result = this.page(pageReq, queryWrapper);

        IPage<D> finalResult = new Page<>();
        finalResult.setTotal(result.getTotal());
        finalResult.setSize(pageSize);
        finalResult.setCurrent(pageNo);
        finalResult.setPages(result.getPages());
        finalResult.setRecords(this.toDTO(result.getRecords()));

        return finalResult;
    }

    /**
     * 处理动态查询条件
     * @param filter 动态查询条件
     * @param queryWrapper 查询对象
     */
    private void processFilter(Filter filter, QueryWrapper<E> queryWrapper) {
        FilterOp op = Optional.ofNullable(filter.getOp()).orElse(FilterOp.EQ);
        List<Filter> subFilters = filter.getFilters();
        if (!CollectionUtils.isEmpty(subFilters)) {
            // 有子查询，处理子查询
            if (op == FilterOp.EQ || op == FilterOp.AND) {
                queryWrapper.and(q -> subFilters.forEach(subFilter -> this.processFilter(subFilter, q)));
            } else if (op == FilterOp.OR) {
                queryWrapper.or(q -> subFilters.forEach(subFilter -> this.processFilter(subFilter, q)));
            }
            return;
        }

        String key = filter.getKey();
        if (StringUtils.isEmpty(key)) {
            log.warn("Key is empty for condition!");
            return;
        }

        // key支持驼峰，自动转下划线
        key = StringUtil.toKabobCase(key).replaceAll("-", "_");

        // 查询条件
        if (op == FilterOp.NULL) {
            queryWrapper.isNull(key);
            return;
        } else if (op == FilterOp.NOT_NULL) {
            queryWrapper.isNotNull(key);
        }

        Object value = filter.getValue();
        Object value1 = filter.getValue1();
        if (null == value && null == value1) {
            return;
        }

        switch (op) {
            case IN -> queryWrapper.in(key, (Collection<?>) value);
            case EQ -> queryWrapper.eq(key, value);
            case LIKE -> queryWrapper.like(key, value);
            case NEQ -> queryWrapper.ne(key, value);
            case BETWEEN -> queryWrapper.between(key, value, value1);
            case LT -> queryWrapper.lt(key, value);
            case LE -> queryWrapper.le(key, value);
            case GT -> queryWrapper.gt(key, value);
            case GE -> queryWrapper.ge(key, value);
        }
    }

    /**
     * 查询记录数
     *
     * @param q 查询对象
     * @return 满足条件的记录数
     */
    @Override
    public long count(Q q) {
        return this.count(this.queryToWrapper(q));
    }

    public abstract D toDTO(E e);

    public List<D> toDTO(List<E> e) {
        return e.stream().map(this::toDTO).collect(Collectors.toList());
    }

    public abstract E toEntity(D dto);

    public List<E> toEntity(List<D> d) {
        return d.stream().map(this::toEntity).collect(Collectors.toList());
    }
}

package com.liuqi.common.base.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liuqi.common.base.bean.dto.BaseDTO;
import com.liuqi.common.base.bean.query.BaseQuery;
import com.liuqi.common.base.bean.query.QueryBuilder;
import com.liuqi.common.base.bean.query.UpdateBuilder;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 抽象实体服务，完成基础数据库操作的封装
 * 用于实体类不含基础字段的场景（创建时间、更新时间、删除标记等）
 *
 * @param <E> Entity实体对象
 * @param <D> DTO数据对象
 * @param <M> 数据库操作Mapper
 * @param <Q> Query查询对象
 */
public abstract class AbstractSimpleEntityService<E, D, M extends BaseMapper<E>, Q extends BaseQuery>
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

        E entity = this.toEntity(dto);
        this.save(entity);
        return this.toDTO(entity);
    }

    @Override
    public List<D> insert(List<D> dtos) {
        // 框架层实现，一条条处理，如果需要批量处理并讲究效率，请使用saveBatch
        if (CollectionUtils.isEmpty(dtos)) {
            return dtos;
        }

        List<D> result = new ArrayList<>(16);
        dtos.forEach(dto -> {
            dto = this.insert(dto);
            result.add(dto);
        });
        return result;
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
        if (!this.processBeforeUpdate(dto)) {
            return;
        }

        E entity = this.toEntity(dto);
        this.updateById(entity);
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
     * 查询后处理
     *
     * @param list 查询结果
     */
    protected void processAfterQuery(List<D> list) {
    }

    /**
     * 执行查询操作
     *
     * @param q 查询对象
     * @return 查询到的列表数据
     */
    @Override
    public List<D> query(Q q) {
        QueryWrapper<E> queryWrapper = this.createQueryWrapper(q);

        if (null != q.getPageNo() && null != q.getPageSize()) {
            long start = (q.getPageNo() - 1) * q.getPageSize();
            queryWrapper.last(" limit " + start + "," + q.getPageSize());
        }

        return this.queryInternal(queryWrapper);
    }

    /**
     * 将查询对象转换成QueryWrapper，并处理排序及排除字段
     * @param q 查询对象
     * @return QueryWrapper
     */
    private QueryWrapper<E> createQueryWrapper(Q q) {
        QueryWrapper<E> queryWrapper = Optional.ofNullable(this.queryToWrapper(q))
                .orElseGet(this::createQueryWrapper);

        boolean hasDeleted = false;

        try {
            this.getEntityClass().getDeclaredField("deleted");
            hasDeleted = true;
        } catch (NoSuchFieldException ignored) {
        }
        if (hasDeleted) {
            queryWrapper.eq("deleted", false);
        }

        if (!CollectionUtils.isEmpty(q.getExcludeFields())) {
            List<String> fields = q.getExcludeFields().stream().map(StringUtils::camelToUnderline).toList();
            queryWrapper.select(field -> !fields.contains(field.getProperty()));
        }

        if (!CollectionUtils.isEmpty(q.getOrderBys())) {
            q.getOrderBys().forEach(orderBy -> {
                String column = orderBy.getColumn();

                // 如果是驼峰需要转成下划线
                column = StringUtils.camelToUnderline(column);
                queryWrapper.orderBy(true, orderBy.isAsc(), column);
            });
        }

        return queryWrapper;
    }

    /**
     * 执行XML中的方法并进行分页处理
     *
     * @param query 查询对象
     * @param func  执行方法
     * @param <D1>  返回数据对象
     * @param <Q1>  查询对象类型
     * @return 查询结果
     */
    protected <D1 extends BaseDTO, Q1 extends BaseQuery> IPage<D1> pageQueryByXml(Q1 query, Function<Q1, List<D1>> func) {
        PageHelper.startPage(query.getPageNo().intValue(), query.getPageSize().intValue());
        PageInfo<D1> pageInfo = new PageInfo<>(func.apply(query));
        Page<D1> page = new Page<>();
        page.setTotal(pageInfo.getTotal());
        page.setRecords(pageInfo.getList());
        return page;
    }

    /**
     * 查询实现
     *
     * @return 查询结果
     */
    protected List<D> queryInternal(QueryWrapper<E> queryWrapper) {
        List<D> list = this.toDTO(this.list(queryWrapper));
        this.processAfterQuery(list);
        return list;
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
        QueryWrapper<E> queryWrapper = this.createQueryWrapper(q);
        return this.pageQueryInternal(q.getPageNo(), q.getPageSize(), queryWrapper);
    }

    private IPage<D> pageQueryInternal(Long pageNo, Long pageSize, QueryWrapper<E> queryWrapper) {
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

        this.processAfterQuery(finalResult.getRecords());
        return finalResult;
    }

    @Override
    public QueryBuilder<D, E> queryBuilder() {
        return QueryBuilder.create(this::queryInternal,
                qb -> this.pageQueryInternal(qb.getPageNo(), qb.getPageSize(), qb.getQueryWrapper()),
                this::count);
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
        queryWrapper.setEntityClass(this.getEntityClass());
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
     * 逻辑
     *
     * @param id 待删除记录id
     */
    @Override
    @Transactional
    public void delete(String id) {
        this.updateBuilder()
                .set("deleted", true)
                .eq("id", id)
                .update();
        this.processAfterDelete(Collections.singletonList(id));
    }

    /**
     * 逻辑删除
     *
     * @param ids 待删除记录id列表
     */
    @Override
    @Transactional
    public void delete(Collection<String> ids) {
        this.updateBuilder()
                .set("deleted", true)
                .in("id", ids)
                .update();
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
     * 查询记录数
     *
     * @param q 查询对象
     * @return 满足条件的记录数
     */
    @Override
    public long count(Q q) {
        return this.count(this.queryToWrapper(q));
    }

    protected D toDTO(E e) {
        D d = createDTO();
        BeanUtils.copyProperties(e, d);
        return d;
    }

    List<D> toDTO(List<E> e) {
        return e.stream().map(this::toDTO).collect(Collectors.toList());
    }

    protected E toEntity(D dto) {
        E e = this.createEntity();
        BeanUtils.copyProperties(dto, e);
        return e;
    }

    List<E> toEntity(List<D> d) {
        return d.stream().map(this::toEntity).collect(Collectors.toList());
    }

    private <T> T createBean(int idx) {
        Type[] types = ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments();
        Class<T> queryClass = (Class<T>) types[idx];
        try {
            return queryClass.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    private E createEntity() {
        return this.createBean(0);
    }

    private D createDTO() {
        return this.createBean(1);
    }

    private Q createQuery() {
        return this.createBean(3);
    }

    /**
     * 创建链式更新对象
     */
    public UpdateBuilder<E> updateBuilder() {
        return new UpdateBuilder<>(this);
    }
}

package com.liuqi.common.base.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.liuqi.common.base.bean.dto.BaseDTO;
import com.liuqi.common.base.bean.query.BaseQuery;
import com.liuqi.common.base.domain.entity.BaseEntity;
import com.liuqi.common.base.domain.mapper.BaseMapper;
import com.liuqi.common.bean.UserContextHolder;
import com.liuqi.common.exception.AppException;
import com.liuqi.common.exception.CommErrorCodes;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

/**
 * 抽象实体服务，完成基础数据库操作的封装
 *
 * @param <E> Entity实体对象
 * @param <D> DTO数据对象
 * @param <M> 数据库操作Mapper
 * @param <Q> Query查询对象
 */
public abstract class AbstractBaseEntityService<E extends BaseEntity, D extends BaseDTO, M extends BaseMapper<E>, Q extends BaseQuery>
        extends AbstractSimpleEntityService<E, D, M, Q> {
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
                    dto.setCreateUser(user.getNickname());
                    dto.setTenantId(user.getTenantId());
                });
        dto.setCreateTime(LocalDateTime.now());
        dto.setDeleted(false);
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
        this.updateById(entity);
    }

    /**
     * 设置更新字段值
     *
     * @param dto 数据对象
     */
    protected void setUpdateFields(D dto) {
        UserContextHolder.get()
                .ifPresent(user -> dto.setUpdateUser(user.getNickname()));
        dto.setUpdateTime(LocalDateTime.now());
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
                .set("deleted", true);
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
     * 创建QueryWrapper
     *
     * @return 创建好的QueryWrapper
     */
    protected QueryWrapper<E> createQueryWrapper() {
        QueryWrapper<E> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("deleted", false);

        return queryWrapper;
    }
}

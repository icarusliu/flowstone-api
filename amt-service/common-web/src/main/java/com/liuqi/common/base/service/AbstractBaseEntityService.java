package com.liuqi.common.base.service;

import com.liuqi.common.base.bean.dto.BaseDTO;
import com.liuqi.common.base.bean.query.BaseQuery;
import com.liuqi.common.base.domain.entity.BaseEntity;
import com.liuqi.common.base.domain.mapper.BaseMapper;
import com.liuqi.common.bean.UserContextHolder;
import com.liuqi.common.exception.AppException;
import com.liuqi.common.exception.CommErrorCodes;
import org.apache.commons.lang3.StringUtils;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

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
                    dto.setCreateUser(user.getUserId());
                    dto.setTenantId(user.getTenantId());
                });
        dto.setCreateTime(LocalDateTime.now());
        dto.setDeleted(false);
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
}
package com.liuqi.acode.base.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.liuqi.acode.base.bean.query.RoleResourceQuery;
import com.liuqi.acode.base.domain.mapper.RoleResourceMapper;
import com.liuqi.acode.base.service.RoleResourceService;
import com.liuqi.acode.base.bean.dto.RoleResourceDTO;
import com.liuqi.acode.base.domain.entity.RoleResourceEntity;
import com.liuqi.common.base.service.AbstractBaseService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
public class RoleResourceServiceImpl extends AbstractBaseService<RoleResourceEntity, RoleResourceDTO, RoleResourceMapper, RoleResourceQuery> implements RoleResourceService {
    @Override
    public RoleResourceDTO toDTO(RoleResourceEntity entity) {
        RoleResourceDTO dto = new RoleResourceDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    @Override
    public RoleResourceEntity toEntity(RoleResourceDTO dto) {
        RoleResourceEntity entity = new RoleResourceEntity();
        BeanUtils.copyProperties(dto, entity);
        return entity;
    }

    @Override
    protected QueryWrapper<RoleResourceEntity> queryToWrapper(RoleResourceQuery query) {
        return this.createQueryWrapper();
    }
}
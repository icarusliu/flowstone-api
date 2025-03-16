package com.liuqi.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.liuqi.sys.bean.dto.OperationDTO;
import com.liuqi.sys.bean.query.OperationQuery;
import com.liuqi.sys.domain.entity.OperationEntity;
import com.liuqi.sys.domain.mapper.OperationMapper;
import com.liuqi.sys.service.OperationService;
import com.liuqi.common.base.service.AbstractBaseService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
public class OperationServiceImpl extends AbstractBaseService<OperationEntity, OperationDTO, OperationMapper, OperationQuery> implements OperationService {
    @Override
    public OperationDTO toDTO(OperationEntity entity) {
        OperationDTO dto = new OperationDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    @Override
    public OperationEntity toEntity(OperationDTO dto) {
        OperationEntity entity = new OperationEntity();
        BeanUtils.copyProperties(dto, entity);
        return entity;
    }

    @Override
    protected QueryWrapper<OperationEntity> queryToWrapper(OperationQuery query) {
        return this.createQueryWrapper();
    }
}
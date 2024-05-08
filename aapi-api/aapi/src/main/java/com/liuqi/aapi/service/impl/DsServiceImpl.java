package com.liuqi.aapi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.liuqi.aapi.domain.entity.DsEntity;
import com.liuqi.aapi.mapper.DsMapper;
import com.liuqi.aapi.service.DsService;
import com.liuqi.aapi.bean.dto.DsDTO;
import com.liuqi.aapi.bean.query.DsQuery;
import com.liuqi.common.base.service.AbstractBaseService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

/** 数据源配置表服务实现 
 * @author Coder Generator 2024-04-20 09:03:05 **/
@Service
public class DsServiceImpl extends AbstractBaseService<DsEntity, DsDTO, DsMapper, DsQuery> implements DsService {
    @Override
    public DsDTO toDTO(DsEntity entity) {
        DsDTO dto = new DsDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    @Override
    public DsEntity toEntity(DsDTO dto) {
        DsEntity entity = new DsEntity();
        BeanUtils.copyProperties(dto, entity);
        return entity;
    }

    @Override
    protected QueryWrapper<DsEntity> queryToWrapper(DsQuery query) {
        return this.createQueryWrapper();
    }
}
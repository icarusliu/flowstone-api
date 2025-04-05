package com.liuqi.etl.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.liuqi.common.base.service.AbstractBaseEntityService;
import com.liuqi.etl.bean.dto.EtlJobTypeDTO;
import com.liuqi.etl.bean.query.EtlJobTypeQuery;
import com.liuqi.etl.domain.entity.EtlJobTypeEntity;
import com.liuqi.etl.domain.mapper.EtlJobTypeMapper;
import com.liuqi.etl.service.EtlJobTypeService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

/**
 * 任务分类服务实现 
 * @author Coder Generator 2025-03-10 15:10:26 
 **/
@Service
public class EtlJobTypeServiceImpl extends AbstractBaseEntityService<EtlJobTypeEntity, EtlJobTypeDTO, EtlJobTypeMapper, EtlJobTypeQuery> implements EtlJobTypeService {
    @Override
    public EtlJobTypeDTO toDTO(EtlJobTypeEntity entity) {
        EtlJobTypeDTO dto = new EtlJobTypeDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    @Override
    public EtlJobTypeEntity toEntity(EtlJobTypeDTO dto) {
        EtlJobTypeEntity entity = new EtlJobTypeEntity();
        BeanUtils.copyProperties(dto, entity);
        return entity;
    }

    @Override
    protected QueryWrapper<EtlJobTypeEntity> queryToWrapper(EtlJobTypeQuery query) {
        return this.createQueryWrapper()
                .eq(StringUtils.isNotBlank(query.getId()), "id", query.getId())
                .in(null != query.getIds(), "id", query.getIds())
                .orderByDesc("create_time");
    }
}
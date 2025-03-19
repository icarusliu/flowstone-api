package com.liuqi.base.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.liuqi.common.base.service.AbstractBaseService;
import com.liuqi.base.bean.dto.ModelPublishedDTO;
import com.liuqi.base.bean.query.ModelPublishedQuery;
import com.liuqi.base.domain.entity.ModelPublishedEntity;
import com.liuqi.base.domain.mapper.ModelPublishedMapper;
import com.liuqi.base.service.ModelPublishedService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

/**
 * 已发布模型服务实现 
 * @author Coder Generator 2025-03-15 10:50:27 
 **/
@Service
public class ModelPublishedServiceImpl extends AbstractBaseService<ModelPublishedEntity, ModelPublishedDTO, ModelPublishedMapper, ModelPublishedQuery> implements ModelPublishedService {
    @Override
    public ModelPublishedDTO toDTO(ModelPublishedEntity entity) {
        ModelPublishedDTO dto = new ModelPublishedDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    @Override
    public ModelPublishedEntity toEntity(ModelPublishedDTO dto) {
        ModelPublishedEntity entity = new ModelPublishedEntity();
        BeanUtils.copyProperties(dto, entity);
        return entity;
    }

    @Override
    protected QueryWrapper<ModelPublishedEntity> queryToWrapper(ModelPublishedQuery query) {
        return this.createQueryWrapper()
                .eq(StringUtils.isNotBlank(query.getId()), "id", query.getId())
                .in(null != query.getIds(), "id", query.getIds())
                .orderByDesc("create_time");
    }
}
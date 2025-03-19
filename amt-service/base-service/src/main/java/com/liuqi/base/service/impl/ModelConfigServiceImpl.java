package com.liuqi.base.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.liuqi.common.base.service.AbstractBaseService;
import com.liuqi.base.bean.dto.ModelConfigDTO;
import com.liuqi.base.bean.query.ModelConfigQuery;
import com.liuqi.base.domain.entity.ModelConfigEntity;
import com.liuqi.base.domain.mapper.ModelConfigMapper;
import com.liuqi.base.service.ModelConfigService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

/**
 * 模型配置服务实现 
 * @author Coder Generator 2025-03-15 16:48:45 
 **/
@Service
public class ModelConfigServiceImpl extends AbstractBaseService<ModelConfigEntity, ModelConfigDTO, ModelConfigMapper, ModelConfigQuery> implements ModelConfigService {
    @Override
    public ModelConfigDTO toDTO(ModelConfigEntity entity) {
        ModelConfigDTO dto = new ModelConfigDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    @Override
    public ModelConfigEntity toEntity(ModelConfigDTO dto) {
        ModelConfigEntity entity = new ModelConfigEntity();
        BeanUtils.copyProperties(dto, entity);
        return entity;
    }

    @Override
    protected QueryWrapper<ModelConfigEntity> queryToWrapper(ModelConfigQuery query) {
        return this.createQueryWrapper()
                .eq(StringUtils.isNotBlank(query.getId()), "id", query.getId())
                .in(null != query.getIds(), "id", query.getIds())
                .orderByDesc("create_time");
    }
}
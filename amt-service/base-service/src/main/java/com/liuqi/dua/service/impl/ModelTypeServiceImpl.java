package com.liuqi.dua.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.liuqi.common.base.service.AbstractBaseService;
import com.liuqi.common.utils.EntityUtils;
import com.liuqi.dua.bean.dto.ApiTypeDTO;
import com.liuqi.dua.bean.dto.ModelTypeDTO;
import com.liuqi.dua.bean.query.ModelTypeQuery;
import com.liuqi.dua.domain.entity.ModelTypeEntity;
import com.liuqi.dua.domain.mapper.ModelTypeMapper;
import com.liuqi.dua.service.ModelTypeService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 模型分类服务实现 
 * @author Coder Generator 2025-03-14 12:16:29 
 **/
@Service
public class ModelTypeServiceImpl extends AbstractBaseService<ModelTypeEntity, ModelTypeDTO, ModelTypeMapper, ModelTypeQuery> implements ModelTypeService {
    @Override
    public ModelTypeDTO toDTO(ModelTypeEntity entity) {
        ModelTypeDTO dto = new ModelTypeDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    @Override
    public ModelTypeEntity toEntity(ModelTypeDTO dto) {
        ModelTypeEntity entity = new ModelTypeEntity();
        BeanUtils.copyProperties(dto, entity);
        return entity;
    }

    @Override
    protected QueryWrapper<ModelTypeEntity> queryToWrapper(ModelTypeQuery query) {
        return this.createQueryWrapper()
                .eq(StringUtils.isNotBlank(query.getId()), "id", query.getId())
                .in(null != query.getIds(), "id", query.getIds())
                .orderByDesc("create_time");
    }

    @Override
    public List<ModelTypeDTO> tree() {
        return EntityUtils.toTableTree(this.findAll());
    }
}
package com.liuqi.base.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.liuqi.common.base.service.AbstractBaseEntityService;
import com.liuqi.base.bean.dto.ModelFieldDTO;
import com.liuqi.base.bean.query.ModelFieldQuery;
import com.liuqi.base.domain.entity.ModelFieldEntity;
import com.liuqi.base.domain.mapper.ModelFieldMapper;
import com.liuqi.base.service.ModelFieldService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 模型字段服务实现 
 * @author Coder Generator 2025-03-14 12:59:51 
 **/
@Service
public class ModelFieldServiceImpl extends AbstractBaseEntityService<ModelFieldEntity, ModelFieldDTO, ModelFieldMapper, ModelFieldQuery> implements ModelFieldService {
    @Override
    public ModelFieldDTO toDTO(ModelFieldEntity entity) {
        ModelFieldDTO dto = new ModelFieldDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    @Override
    public ModelFieldEntity toEntity(ModelFieldDTO dto) {
        ModelFieldEntity entity = new ModelFieldEntity();
        BeanUtils.copyProperties(dto, entity);
        return entity;
    }

    @Override
    protected QueryWrapper<ModelFieldEntity> queryToWrapper(ModelFieldQuery query) {
        return this.createQueryWrapper()
                .eq(StringUtils.isNotBlank(query.getId()), "id", query.getId())
                .in(null != query.getIds(), "id", query.getIds())
                .eq(StringUtils.isNotBlank(query.getModelId()), "model_id", query.getModelId())
                .orderByAsc("sort");
    }

    /**
     * 删除不在指定范围内的字段
     *
     * @param modelId  模型id
     * @param fieldIds 不能删除的字段id，其它字段都删除
     */
    @Override
    public void deleteModelFieldsNotIn(String modelId, List<String> fieldIds) {
        UpdateWrapper<ModelFieldEntity> queryWrapper = Wrappers.update();
        queryWrapper.eq("model_id", modelId)
                .notIn("id", fieldIds)
                .set("deleted", 1);
        this.update(queryWrapper);
    }

    /**
     * 更新记录
     *
     * @param dto 待更新记录内容，id不能为空
     */
    @Override
    public void update(ModelFieldDTO dto) {
        // id有可能是前端传过来的，此时需要做插入
        boolean exists = this.findById(dto.getId()).isPresent();
        if (exists) {
            super.update(dto);
        } else {
            this.insert(dto);
        }
    }
}
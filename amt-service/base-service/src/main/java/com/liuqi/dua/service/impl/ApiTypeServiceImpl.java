package com.liuqi.dua.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.liuqi.common.base.service.AbstractBaseService;
import com.liuqi.common.utils.EntityUtils;
import com.liuqi.dua.bean.dto.ApiTypeDTO;
import com.liuqi.dua.bean.query.ApiTypeQuery;
import com.liuqi.dua.domain.entity.ApiTypeEntity;
import com.liuqi.dua.domain.mapper.ApiTypeMapper;
import com.liuqi.dua.service.ApiTypeService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 接口分类服务实现
 *
 * @author Coder Generator 2024-08-09 22:08:31
 **/
@Service
public class ApiTypeServiceImpl extends AbstractBaseService<ApiTypeEntity, ApiTypeDTO, ApiTypeMapper, ApiTypeQuery> implements ApiTypeService {
    @Override
    public ApiTypeDTO toDTO(ApiTypeEntity entity) {
        ApiTypeDTO dto = new ApiTypeDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    @Override
    public ApiTypeEntity toEntity(ApiTypeDTO dto) {
        ApiTypeEntity entity = new ApiTypeEntity();
        BeanUtils.copyProperties(dto, entity);
        return entity;
    }

    @Override
    protected QueryWrapper<ApiTypeEntity> queryToWrapper(ApiTypeQuery query) {
        return this.createQueryWrapper()
                .eq(StringUtils.isNotBlank(query.getId()), "id", query.getId())
                .in(null != query.getIds(), "id", query.getIds())
                .eq("deleted", false)
                .orderByAsc("sort");
    }

    /**
     * 获取分类树型结构
     *
     * @return 分类树型结构
     */
    @Override
    public List<ApiTypeDTO> tree() {
        return EntityUtils.toTableTree(this.findAll());
    }
}
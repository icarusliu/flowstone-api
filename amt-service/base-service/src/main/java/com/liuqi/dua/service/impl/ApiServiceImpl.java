package com.liuqi.dua.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.liuqi.common.base.service.AbstractBaseService;
import com.liuqi.dua.bean.dto.ApiDTO;
import com.liuqi.dua.bean.query.ApiQuery;
import com.liuqi.dua.domain.entity.ApiEntity;
import com.liuqi.dua.domain.mapper.ApiMapper;
import com.liuqi.dua.service.ApiService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

/**
 * 接口服务实现
 *
 * @author Coder Generator 2024-07-08 22:32:36
 **/
@Service
public class ApiServiceImpl extends AbstractBaseService<ApiEntity, ApiDTO, ApiMapper, ApiQuery> implements ApiService {
    @Override
    public ApiDTO toDTO(ApiEntity entity) {
        ApiDTO dto = new ApiDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    @Override
    public ApiEntity toEntity(ApiDTO dto) {
        ApiEntity entity = new ApiEntity();
        BeanUtils.copyProperties(dto, entity);
        return entity;
    }

    @Override
    protected QueryWrapper<ApiEntity> queryToWrapper(ApiQuery query) {
        return this.createQueryWrapper()
                .eq(StringUtils.isNotBlank(query.getId()), "id", query.getId())
                .in(null != query.getIds(), "id", query.getIds())
                .eq("deleted", false)
                .and(StringUtils.isNotBlank(query.getKey()), q -> {
                    q.eq("id", query.getKey())
                            .or().eq("path", query.getKey());
                })
                .orderByDesc("create_time");
    }
}
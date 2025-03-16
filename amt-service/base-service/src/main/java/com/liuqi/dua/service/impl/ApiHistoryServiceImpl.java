package com.liuqi.dua.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.liuqi.common.base.service.AbstractBaseService;
import com.liuqi.dua.bean.dto.ApiHistoryDTO;
import com.liuqi.dua.bean.query.ApiHistoryQuery;
import com.liuqi.dua.domain.entity.ApiHistoryEntity;
import com.liuqi.dua.domain.mapper.ApiHistoryMapper;
import com.liuqi.dua.service.ApiHistoryService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

/**
 * 接口历史服务实现 
 * @author Coder Generator 2025-03-16 17:16:13 
 **/
@Service
public class ApiHistoryServiceImpl extends AbstractBaseService<ApiHistoryEntity, ApiHistoryDTO, ApiHistoryMapper, ApiHistoryQuery> implements ApiHistoryService {
    @Override
    public ApiHistoryDTO toDTO(ApiHistoryEntity entity) {
        ApiHistoryDTO dto = new ApiHistoryDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    @Override
    public ApiHistoryEntity toEntity(ApiHistoryDTO dto) {
        ApiHistoryEntity entity = new ApiHistoryEntity();
        BeanUtils.copyProperties(dto, entity);
        return entity;
    }

    @Override
    protected QueryWrapper<ApiHistoryEntity> queryToWrapper(ApiHistoryQuery query) {
        return this.createQueryWrapper()
                .eq(StringUtils.isNotBlank(query.getId()), "id", query.getId())
                .in(null != query.getIds(), "id", query.getIds())
                .eq(StringUtils.isNotBlank(query.getApiId()), "api_id", query.getApiId())
                .orderByDesc("create_time");
    }
}
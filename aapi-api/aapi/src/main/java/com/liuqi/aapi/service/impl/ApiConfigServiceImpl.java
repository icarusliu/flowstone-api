package com.liuqi.aapi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.liuqi.aapi.domain.entity.ApiConfigEntity;
import com.liuqi.aapi.mapper.ApiConfigMapper;
import com.liuqi.aapi.service.ApiConfigService;
import com.liuqi.aapi.bean.dto.ApiConfigDTO;
import com.liuqi.aapi.bean.query.ApiConfigQuery;
import com.liuqi.common.base.service.AbstractBaseService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Optional;

/** API配置表服务实现 
 * @author Coder Generator 2024-04-18 14:25:01 **/
@Service
public class ApiConfigServiceImpl extends AbstractBaseService<ApiConfigEntity, ApiConfigDTO, ApiConfigMapper, ApiConfigQuery>
        implements ApiConfigService {
    @Override
    public Optional<ApiConfigDTO> findByPath(String tenantId, String path) {
        ApiConfigQuery query = new ApiConfigQuery();
        query.setTenantId(tenantId);
        query.setPath(path);
        return this.findOne(query);
    }

    @Override
    public ApiConfigDTO toDTO(ApiConfigEntity entity) {
        ApiConfigDTO dto = new ApiConfigDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    @Override
    public ApiConfigEntity toEntity(ApiConfigDTO dto) {
        ApiConfigEntity entity = new ApiConfigEntity();
        BeanUtils.copyProperties(dto, entity);
        return entity;
    }

    @Override
    protected QueryWrapper<ApiConfigEntity> queryToWrapper(ApiConfigQuery query) {
        return this.createQueryWrapper()
                .eq(StringUtils.isNotBlank(query.getPath()), "path", query.getPath())
                .eq(StringUtils.isNotBlank(query.getTenantId()), "tenant_id", query.getTenantId());
    }
}
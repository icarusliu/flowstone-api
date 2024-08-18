package com.liuqi.dua.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.liuqi.common.base.service.AbstractBaseService;
import com.liuqi.dua.bean.dto.SupplierDTO;
import com.liuqi.dua.bean.query.SupplierQuery;
import com.liuqi.dua.domain.entity.SupplierEntity;
import com.liuqi.dua.domain.mapper.SupplierMapper;
import com.liuqi.dua.service.SupplierService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

/**
 * 接入方服务实现
 *
 * @author Coder Generator 2024-08-12 19:00:03
 **/
@Service
public class SupplierServiceImpl extends AbstractBaseService<SupplierEntity, SupplierDTO, SupplierMapper, SupplierQuery> implements SupplierService {
    @Override
    public SupplierDTO toDTO(SupplierEntity entity) {
        SupplierDTO dto = new SupplierDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    @Override
    public SupplierEntity toEntity(SupplierDTO dto) {
        SupplierEntity entity = new SupplierEntity();
        BeanUtils.copyProperties(dto, entity);
        return entity;
    }

    @Override
    protected QueryWrapper<SupplierEntity> queryToWrapper(SupplierQuery query) {
        return this.createQueryWrapper()
                .eq(StringUtils.isNotBlank(query.getId()), "id", query.getId())
                .in(null != query.getIds(), "id", query.getIds())
                .eq("deleted", false)
                .orderByDesc("create_time");
    }
}
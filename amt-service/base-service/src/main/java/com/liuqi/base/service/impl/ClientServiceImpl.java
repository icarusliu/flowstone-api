package com.liuqi.base.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.liuqi.base.bean.dto.ClientDTO;
import com.liuqi.base.bean.query.ClientQuery;
import com.liuqi.base.domain.entity.ClientEntity;
import com.liuqi.base.domain.mapper.ClientMapper;
import com.liuqi.base.service.ClientService;
import com.liuqi.common.base.service.AbstractBaseService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

/**
 * 客户端服务实现 
 * @author Coder Generator 2024-09-25 09:04:45 
 **/
@Service
public class ClientServiceImpl extends AbstractBaseService<ClientEntity, ClientDTO, ClientMapper, ClientQuery> implements ClientService {
    @Override
    public ClientDTO toDTO(ClientEntity entity) {
        ClientDTO dto = new ClientDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    @Override
    public ClientEntity toEntity(ClientDTO dto) {
        ClientEntity entity = new ClientEntity();
        BeanUtils.copyProperties(dto, entity);
        return entity;
    }

    @Override
    protected QueryWrapper<ClientEntity> queryToWrapper(ClientQuery query) {
        return this.createQueryWrapper()
                .eq(StringUtils.isNotBlank(query.getId()), "id", query.getId())
                .in(null != query.getIds(), "id", query.getIds())
                .eq("deleted", false)
                .orderByDesc("create_time");
    }
}
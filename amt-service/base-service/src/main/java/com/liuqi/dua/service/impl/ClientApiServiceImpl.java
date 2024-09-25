package com.liuqi.dua.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.liuqi.common.base.service.AbstractBaseService;
import com.liuqi.dua.bean.dto.ClientApiDTO;
import com.liuqi.dua.bean.query.ClientApiQuery;
import com.liuqi.dua.domain.entity.ClientApiEntity;
import com.liuqi.dua.domain.mapper.ClientApiMapper;
import com.liuqi.dua.service.ClientApiService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 客户端接口列表服务实现 
 * @author Coder Generator 2024-09-25 15:58:18 
 **/
@Service
public class ClientApiServiceImpl extends AbstractBaseService<ClientApiEntity, ClientApiDTO, ClientApiMapper, ClientApiQuery> implements ClientApiService {
    @Override
    public ClientApiDTO toDTO(ClientApiEntity entity) {
        ClientApiDTO dto = new ClientApiDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    @Override
    public ClientApiEntity toEntity(ClientApiDTO dto) {
        ClientApiEntity entity = new ClientApiEntity();
        BeanUtils.copyProperties(dto, entity);
        return entity;
    }

    @Override
    protected QueryWrapper<ClientApiEntity> queryToWrapper(ClientApiQuery query) {
        return this.createQueryWrapper()
                .eq(StringUtils.isNotBlank(query.getId()), "id", query.getId())
                .in(null != query.getIds(), "id", query.getIds())
                .eq(StringUtils.isNotBlank(query.getClientId()), "client_id", query.getClientId())
                .orderByDesc("create_time");
    }

    @Override
    public void delete(String clientId, List<String> apiIds) {
        UpdateWrapper<ClientApiEntity> updateWrapper = Wrappers.<ClientApiEntity>update()
                .eq(StringUtils.isNotBlank(clientId), "client_id", clientId)
                .in("api_id", apiIds);
        baseMapper.delete(updateWrapper);
    }
}
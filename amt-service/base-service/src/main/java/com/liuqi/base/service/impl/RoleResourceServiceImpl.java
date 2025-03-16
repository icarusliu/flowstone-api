package com.liuqi.base.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.liuqi.base.bean.dto.RoleResourceDTO;
import com.liuqi.base.bean.query.RoleResourceQuery;
import com.liuqi.base.domain.entity.RoleResourceEntity;
import com.liuqi.base.domain.mapper.RoleResourceMapper;
import com.liuqi.base.service.RoleResourceService;
import com.liuqi.common.base.service.AbstractBaseService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class RoleResourceServiceImpl extends AbstractBaseService<RoleResourceEntity, RoleResourceDTO, RoleResourceMapper, RoleResourceQuery> implements RoleResourceService {
    @Override
    public RoleResourceDTO toDTO(RoleResourceEntity entity) {
        RoleResourceDTO dto = new RoleResourceDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    @Override
    public RoleResourceEntity toEntity(RoleResourceDTO dto) {
        RoleResourceEntity entity = new RoleResourceEntity();
        BeanUtils.copyProperties(dto, entity);
        return entity;
    }

    @Override
    protected QueryWrapper<RoleResourceEntity> queryToWrapper(RoleResourceQuery query) {
        return this.createQueryWrapper()
                .eq(StringUtils.isNotBlank(query.getRoleId()), "role_id", query.getRoleId())
                .in(!CollectionUtils.isEmpty(query.getRoleIds()), "role_id", query.getRoleIds());
    }

    @Override
    public void updateRoleResources(String roleId, Map<String, List<String>> resourceIds) {
        if (CollectionUtils.isEmpty(resourceIds)) {
            return;
        }

        // 先删除后添加
        this.remove(Wrappers.<RoleResourceEntity>query().eq("role_id", roleId).in("resource_type", resourceIds.keySet()));

        List<RoleResourceDTO> list = new ArrayList<>(16);
        resourceIds.forEach((k, ids) -> {
            if (CollectionUtils.isEmpty(ids)) {
                return;
            }

            ids.forEach(id -> {
                RoleResourceDTO dto = new RoleResourceDTO();
                list.add(dto);
                dto.setRoleId(roleId);
                dto.setResourceId(id);
                dto.setResourceType(k);
            });
        });

        if (!CollectionUtils.isEmpty(list)) {
            this.insert(list);
        }
    }

    @Override
    public List<RoleResourceDTO> findByRole(String roleId) {
        RoleResourceQuery query = new RoleResourceQuery();
        query.setRoleId(roleId);
        return this.query(query);
    }

    /**
     * 根据角色查找资源列表
     *
     * @param roleIds 角色id列表
     * @return 资源列表
     */
    @Override
    public List<RoleResourceDTO> findByRoles(List<String> roleIds) {
        if (CollectionUtils.isEmpty(roleIds)) {
            return new ArrayList<>(0);
        }
        
        RoleResourceQuery query = new RoleResourceQuery();
        query.setRoleIds(roleIds);
        return this.query(query);
    }
}
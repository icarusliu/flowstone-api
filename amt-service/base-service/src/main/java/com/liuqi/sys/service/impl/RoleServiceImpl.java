package com.liuqi.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.liuqi.common.base.service.AbstractBaseEntityService;
import com.liuqi.common.exception.AppException;
import com.liuqi.common.utils.TreeUtils;
import com.liuqi.sys.bean.dto.MenuDTO;
import com.liuqi.sys.bean.dto.RoleDTO;
import com.liuqi.sys.bean.dto.RoleResourceDTO;
import com.liuqi.sys.bean.query.RoleQuery;
import com.liuqi.sys.bean.resp.RoleResourceInfo;
import com.liuqi.sys.common.ErrorCodes;
import com.liuqi.sys.domain.entity.RoleEntity;
import com.liuqi.sys.domain.mapper.RoleMapper;
import com.liuqi.sys.service.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
public class RoleServiceImpl extends AbstractBaseEntityService<RoleEntity, RoleDTO, RoleMapper, RoleQuery> implements RoleService {
    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private MenuService menuService;

    @Autowired
    private MenuButtonService buttonService;

    @Autowired
    private RoleResourceService roleResourceService;

    @Override
    public RoleDTO toDTO(RoleEntity entity) {
        RoleDTO dto = RoleDTO.builder().build();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    @Override
    public RoleEntity toEntity(RoleDTO dto) {
        RoleEntity entity = RoleEntity.builder().build();
        BeanUtils.copyProperties(dto, entity);
        return entity;
    }

    @Override
    protected QueryWrapper<RoleEntity> queryToWrapper(RoleQuery query) {
        return this.createQueryWrapper()
                .eq(StringUtils.isNotBlank(query.getCode()), "code", query.getCode())
                .eq(StringUtils.isNotBlank(query.getAppId()), "app_id", query.getAppId())
                .eq(StringUtils.isNotBlank(query.getName()), "name", query.getName());
    }

    /**
     * 插入前处理
     * 可以执行相关参数校验，校验不通过时抛出异常
     *
     * @param dto 角色
     * @return false时不进行插入，true时进行插入
     */
    @Override
    protected boolean processBeforeInsert(RoleDTO dto) {
        String appId = Optional.ofNullable(dto.getAppId()).orElse("sys");
        dto.setAppId(appId);

        // 角色名称不能重复
        Optional<RoleDTO> entities = this.findByName(dto.getAppId(), dto.getName());
        if (entities.isPresent()) {
            throw AppException.of(ErrorCodes.BASE_ROLE_EXISTS);
        }

        return super.processBeforeInsert(dto);
    }

    /**
     * 根据名称查找角色
     *
     * @param name 名称
     * @return 角色信息
     */
    private Optional<RoleDTO> findByName(String appId, String name) {
        return this.findOne(RoleQuery.builder().appId(appId).name(name).build());
    }

    /**
     * 更新前处理
     *
     * @param dto 角色信息
     * @return false时不再进行更新，true时进行更新
     */
    @Override
    protected boolean processBeforeUpdate(RoleDTO dto) {
        // 角色名称不能重复
        String name = dto.getName();
        String id = dto.getId();

        String appId = Optional.ofNullable(dto.getAppId()).orElse("sys");
        dto.setAppId(appId);

        if (StringUtils.isNotBlank(name)) {
            boolean exists = this.findByName(dto.getAppId(), name)
                    .filter(d -> !d.getId().equals(id)).isPresent();
            if (exists) {
                throw AppException.of(ErrorCodes.BASE_ROLE_NAME_EXISTS);
            }
        }

        return super.processBeforeUpdate(dto);
    }

    /**
     * 删除后处理
     *
     * @param roleIds 角色id列表
     */
    @Override
    protected void processAfterDelete(Collection<String> roleIds) {
        // 删除角色时需要删除对应的用户角色信息
        userRoleService.deleteByRole(roleIds);

        super.processAfterDelete(roleIds);
    }

    /**
     * 根据角色获取角色菜单按钮等资源信息
     *
     * @param roleId 角色id
     * @return 返回所有菜单、按钮等资源，角色如果有权限时，checked标志为true
     */
    @Override
    public List<RoleResourceInfo> getRoleMenus(String appId, String roleId) {
        // 先查找所有菜单、按钮信息，然后根据角色所配置的角色
        List<MenuDTO> tree = menuService.getTree(appId, false, true);
        if (CollectionUtils.isEmpty(tree)) {
            return new ArrayList<>(0);
        }

        // 查找角色资源权限
        List<RoleResourceDTO> roleResources = roleResourceService.findByRole(roleId);
        List<String> resourceIds = roleResources.stream().map(RoleResourceDTO::getResourceId).toList();

        // 处理菜单选中及菜单按钮
        return TreeUtils.map(tree, menu -> {
            RoleResourceInfo info = new RoleResourceInfo();
            info.setId(menu.getId());
            info.setName(menu.getName());
            info.setParentId(menu.getParentId());
            info.setChecked(resourceIds.contains(menu.getId()));

            if (!CollectionUtils.isEmpty(menu.getButtons())) {
                info.setButtons(menu.getButtons().stream().map(button -> {
                    RoleResourceInfo buttonInfo = new RoleResourceInfo();
                    buttonInfo.setId(button.getId());
                    buttonInfo.setName(button.getName());
                    buttonInfo.setChecked(resourceIds.contains(button.getId()));
                    return buttonInfo;
                }).toList());
            }

            return info;
        });
    }
}

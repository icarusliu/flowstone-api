package com.liuqi.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.liuqi.sys.bean.dto.UserRoleDTO;
import com.liuqi.sys.bean.query.UserRoleQuery;
import com.liuqi.sys.domain.entity.UserRoleEntity;
import com.liuqi.sys.domain.mapper.UserRoleMapper;
import com.liuqi.sys.service.UserRoleService;
import com.liuqi.common.base.service.AbstractBaseService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * 用户角色服务
 *
 * @author 不空军 2023/12/27 15:03
 **/
@Service
public class UserRoleServiceImpl extends AbstractBaseService<UserRoleEntity, UserRoleDTO, UserRoleMapper, UserRoleQuery> implements UserRoleService {
    @Override
    public UserRoleDTO toDTO(UserRoleEntity entity) {
        UserRoleDTO dto = new UserRoleDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    @Override
    public UserRoleEntity toEntity(UserRoleDTO dto) {
        UserRoleEntity entity = new UserRoleEntity();
        BeanUtils.copyProperties(dto, entity);
        return entity;
    }

    @Override
    protected QueryWrapper<UserRoleEntity> queryToWrapper(UserRoleQuery query) {
        return this.createQueryWrapper()
                .in(CollectionUtils.isNotEmpty(query.getUserIds()), "user_id", query.getUserIds())
                .eq(StringUtils.isNotBlank(query.getUserId()), "user_id", query.getUserId())
                .in(CollectionUtils.isNotEmpty(query.getRoleIds()), "role_id", query.getRoleIds())
                .eq(StringUtils.isNotBlank(query.getRoleId()), "role_id", query.getRoleId());
    }

    /**
     * 添加用户角色
     *
     * @param userId  用户id
     * @param roleIds 角色id
     */
    @Override
    public void addUserRoles(String userId, List<String> roleIds) {
        // 不能重复添加
        if (CollectionUtils.isEmpty(roleIds)) {
            return;
        }

        List<UserRoleEntity> roles = roleIds
                .stream().map(id -> {
                    UserRoleEntity entity = new UserRoleEntity();
                    entity.setRoleId(id);
                    entity.setUserId(userId);
                    return entity;
                }).toList();
        this.saveBatch(roles);
    }

    /**
     * 删除用户角色关系
     *
     * @param userIds 待删除用户id列表
     */
    @Override
    public void deleteByUser(Collection<String> userIds) {
        if (CollectionUtils.isEmpty(userIds)) {
            return;
        }
        this.remove(this.createQueryWrapper().in("user_id", userIds));
    }

    /**
     * 根据角色删除关联关系
     *
     * @param roleIds 角色id列表
     */
    @Override
    public void deleteByRole(Collection<String> roleIds) {
        if (CollectionUtils.isEmpty(roleIds)) {
            return;
        }

        this.remove(this.createQueryWrapper().in("role_id", roleIds));
    }

    /**
     * 根据用户id及角色id列表删除对应关系
     *
     * @param userId  用户id
     * @param roleIds 角色id列表
     */
    @Override
    public void deleteUserRoles(String userId, List<String> roleIds) {
        if (CollectionUtils.isEmpty(roleIds)) {
            return;
        }
        this.remove(this.createQueryWrapper().in("role_id", roleIds).eq("user_id", userId));
    }

    /**
     * 保存用户角色，不存在的进行删除
     *
     * @param userId  用户id
     * @param roleIds 角色id列表
     */
    @Override
    @Transactional
    public void saveUserRoles(String userId, List<String> roleIds) {
        // 先根据用户删除角色，再进行保存
        this.deleteByUser(Collections.singleton(userId));

        if (CollectionUtils.isEmpty(roleIds)) {
            return;
        }

        this.addUserRoles(userId, roleIds);
    }

    @Override
    public List<UserRoleDTO> findByUsers(List<String> userIds) {
        UserRoleQuery query = new UserRoleQuery();
        query.setUserIds(userIds);
        return this.query(query);
    }

    /**
     * 保存角色用户清单
     *
     * @param roleId  角色id
     * @param userIds 用户列表
     */
    @Override
    public void saveRoleUsers(String roleId, List<String> userIds) {
        List<UserRoleDTO> list = this.findByRoles(List.of(roleId));
        List<String> existsUsers = list.stream().map(UserRoleDTO::getUserId).toList();
        userIds.removeAll(existsUsers);
        if (CollectionUtils.isEmpty(userIds)) {
            return;
        }

        List<UserRoleDTO> toSaveList = userIds.stream()
                .map(userId -> {
                    UserRoleDTO dto = new UserRoleDTO();
                    dto.setRoleId(roleId);
                    dto.setUserId(userId);
                    return dto;
                }).toList();

        this.insert(toSaveList);
    }

    /**
     * 删除角色用户信息
     *
     * @param roleId  角色id
     * @param userIds 用户id
     */
    @Override
    public void removeRoleUsers(String roleId, List<String> userIds) {
        if (CollectionUtils.isEmpty(userIds)) {
            return;
        }
        this.remove(this.createQueryWrapper().in("user_id", userIds).eq("role_id", roleId));
    }
}
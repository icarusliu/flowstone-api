package com.liuqi.acode.base.service;

import com.liuqi.acode.base.bean.query.UserRoleQuery;
import com.liuqi.acode.base.bean.dto.UserRoleDTO;
import com.liuqi.acode.base.domain.entity.UserRoleEntity;
import com.liuqi.common.base.service.BaseService;

import java.util.Collection;
import java.util.List;

public interface UserRoleService extends BaseService<UserRoleEntity, UserRoleDTO, UserRoleQuery> {

    void addUserRoles(String userId, List<String> roleIds);

    /**
     * 根据用户id列表进行删除
     *
     * @param userIds 待删除用户id列表
     */
    void deleteByUser(Collection<String> userIds);

    /**
     * 根据角色删除对应关系
     *
     * @param roleIds 角色id列表
     */
    void deleteByRole(Collection<String> roleIds);

    /**
     * 根据用户id及角色id列表删除对应关系
     *
     * @param userId  用户id
     * @param roleIds 角色id列表
     */
    void deleteUserRoles(String userId, List<String> roleIds);

    /**
     * 保存用户角色，不存在的进行删除
     *
     * @param userId  用户id
     * @param roleIds 角色id列表
     */
    void saveUserRoles(String userId, List<String> roleIds);
}
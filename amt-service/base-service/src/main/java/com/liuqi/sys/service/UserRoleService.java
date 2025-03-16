package com.liuqi.sys.service;

import com.liuqi.sys.bean.dto.UserRoleDTO;
import com.liuqi.sys.bean.query.UserRoleQuery;
import com.liuqi.common.base.service.BaseService;

import java.util.Collection;
import java.util.List;

public interface UserRoleService extends BaseService<UserRoleDTO, UserRoleQuery> {

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

    /**
     * 根据用户批量查找角色清单
     *
     * @param userIds 用户id列表
     * @return 用户角色列表
     */
    List<UserRoleDTO> findByUsers(List<String> userIds);
}
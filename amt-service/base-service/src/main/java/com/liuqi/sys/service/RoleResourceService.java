package com.liuqi.sys.service;

import com.liuqi.sys.bean.dto.RoleResourceDTO;
import com.liuqi.sys.bean.query.RoleResourceQuery;
import com.liuqi.common.base.service.BaseService;

import java.util.List;
import java.util.Map;

public interface RoleResourceService extends BaseService<RoleResourceDTO, RoleResourceQuery> {
    /**
     * 更新角色资源信息
     *
     * @param roleId      角色id
     * @param resourceIds 资源类型对应的资源列表
     */
    void updateRoleResources(String roleId, Map<String, List<String>> resourceIds);

    /**
     * 根据角色查找资源列表
     *
     * @param roleId 角色id
     * @return 资源列表
     */
    List<RoleResourceDTO> findByRole(String roleId);

    /**
     * 根据角色查找资源列表
     *
     * @param roleIds 角色id列表
     * @return 资源列表
     */
    List<RoleResourceDTO> findByRoles(List<String> roleIds);
}
package com.liuqi.base.service;

import com.liuqi.base.bean.dto.RoleDTO;
import com.liuqi.base.bean.query.RoleQuery;
import com.liuqi.base.bean.resp.RoleResourceInfo;
import com.liuqi.common.base.service.BaseService;

import java.util.List;

public interface RoleService extends BaseService<RoleDTO, RoleQuery> {
    /**
     * 查找角色菜单信息
     *
     * @param roleId 角色id
     * @return 角色菜单信息
     */
    List<RoleResourceInfo> getRoleMenus(String roleId);
}

package com.liuqi.sys.service;

import com.liuqi.sys.bean.dto.MenuDTO;
import com.liuqi.sys.bean.query.MenuQuery;
import com.liuqi.common.base.service.BaseService;

import java.util.List;

public interface MenuService extends BaseService<MenuDTO, MenuQuery> {
    List<MenuDTO> findByParent(String parentId);

    /**
     * 获取树形结构
     *
     * @return 树形菜单
     */
    List<MenuDTO> getTree(Boolean withHide, Boolean withButtons);
}
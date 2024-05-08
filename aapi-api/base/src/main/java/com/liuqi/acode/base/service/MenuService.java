package com.liuqi.acode.base.service;

import com.liuqi.acode.base.bean.dto.MenuDTO;
import com.liuqi.acode.base.bean.query.MenuQuery;
import com.liuqi.acode.base.domain.entity.MenuEntity;
import com.liuqi.common.base.bean.dto.Tree;
import com.liuqi.common.base.service.BaseService;

import java.util.List;

public interface MenuService extends BaseService<MenuEntity, MenuDTO, MenuQuery> {
    List<MenuDTO> findByParent(String parentId);

    /**
     * 获取树形结构
     *
     * @return 树形菜单
     */
    List<Tree<MenuDTO>> getTree();
}
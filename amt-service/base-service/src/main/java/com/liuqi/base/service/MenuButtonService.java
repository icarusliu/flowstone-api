package com.liuqi.base.service;

import com.liuqi.base.bean.dto.MenuButtonDTO;
import com.liuqi.base.bean.query.MenuButtonQuery;
import com.liuqi.base.bean.req.MenuButtonAddReq;
import com.liuqi.common.base.service.BaseService;

import java.util.List;

/**
 * 菜单按钮服务接口
 *
 * @author Coder Generator 2024-09-29 18:55:25
 **/
public interface MenuButtonService extends BaseService<MenuButtonDTO, MenuButtonQuery> {
    /**
     * 保存菜单按钮
     *
     * @param menuId  菜单id
     * @param buttons 按钮列表
     */
    void saveMenuButtons(String menuId, List<MenuButtonAddReq> buttons);
}
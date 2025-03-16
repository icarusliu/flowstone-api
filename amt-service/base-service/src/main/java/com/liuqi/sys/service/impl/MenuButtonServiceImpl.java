package com.liuqi.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.liuqi.sys.bean.dto.MenuButtonDTO;
import com.liuqi.sys.bean.query.MenuButtonQuery;
import com.liuqi.sys.bean.req.MenuButtonAddReq;
import com.liuqi.sys.domain.entity.MenuButtonEntity;
import com.liuqi.sys.domain.mapper.MenuButtonMapper;
import com.liuqi.sys.service.MenuButtonService;
import com.liuqi.common.base.service.AbstractBaseService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * 菜单按钮服务实现
 *
 * @author Coder Generator 2024-09-29 18:55:25
 **/
@Service
public class MenuButtonServiceImpl extends AbstractBaseService<MenuButtonEntity, MenuButtonDTO, MenuButtonMapper, MenuButtonQuery> implements MenuButtonService {
    @Override
    public MenuButtonDTO toDTO(MenuButtonEntity entity) {
        MenuButtonDTO dto = new MenuButtonDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    @Override
    public MenuButtonEntity toEntity(MenuButtonDTO dto) {
        MenuButtonEntity entity = new MenuButtonEntity();
        BeanUtils.copyProperties(dto, entity);
        return entity;
    }

    @Override
    protected QueryWrapper<MenuButtonEntity> queryToWrapper(MenuButtonQuery query) {
        return this.createQueryWrapper()
                .eq(StringUtils.isNotBlank(query.getId()), "id", query.getId())
                .in(null != query.getIds(), "id", query.getIds())
                .eq("deleted", false)
                .orderByDesc("create_time");
    }

    /**
     * 保存菜单按钮
     *
     * @param menuId  菜单id
     * @param buttons 按钮列表
     */
    @Override
    @Transactional
    public void saveMenuButtons(String menuId, List<MenuButtonAddReq> buttons) {
        // 先删除后添加
        this.remove(Wrappers.<MenuButtonEntity>query().eq("menu_id", menuId));

        if (CollectionUtils.isEmpty(buttons)) {
            return;
        }

        List<MenuButtonDTO> list = buttons.stream().map(item -> {
            MenuButtonDTO dto = new MenuButtonDTO();
            BeanUtils.copyProperties(item, dto);
            dto.setMenuId(menuId);
            return dto;
        }).toList();
        this.insert(list);
    }
}
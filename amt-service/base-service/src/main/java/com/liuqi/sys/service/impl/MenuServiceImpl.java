package com.liuqi.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.liuqi.common.base.service.AbstractBaseEntityService;
import com.liuqi.common.bean.UserContext;
import com.liuqi.common.bean.UserContextHolder;
import com.liuqi.common.exception.AppException;
import com.liuqi.common.utils.TreeUtils;
import com.liuqi.common.utils.enhancer.ListEnhancer;
import com.liuqi.common.utils.value.BiValue;
import com.liuqi.sys.bean.dto.MenuButtonDTO;
import com.liuqi.sys.bean.dto.MenuDTO;
import com.liuqi.sys.bean.query.MenuQuery;
import com.liuqi.sys.domain.entity.MenuEntity;
import com.liuqi.sys.domain.mapper.MenuMapper;
import com.liuqi.sys.service.MenuButtonService;
import com.liuqi.sys.service.MenuService;
import com.liuqi.sys.service.UserService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.liuqi.sys.common.ErrorCodes.BASE_MENU_NAME_EXISTS;

@Service
public class MenuServiceImpl extends AbstractBaseEntityService<MenuEntity, MenuDTO, MenuMapper, MenuQuery> implements MenuService {
    @Autowired
    private MenuButtonService menuButtonService;

    @Autowired
    private UserService userService;

    @Override
    public MenuDTO toDTO(MenuEntity entity) {
        MenuDTO dto = new MenuDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    @Override
    public MenuEntity toEntity(MenuDTO dto) {
        MenuEntity entity = new MenuEntity();
        BeanUtils.copyProperties(dto, entity);
        return entity;
    }

    @Override
    protected QueryWrapper<MenuEntity> queryToWrapper(MenuQuery query) {
        return this.createQueryWrapper()
                .eq(StringUtils.isNotBlank(query.getCode()), "code", query.getCode())
                .eq(StringUtils.isNotBlank(query.getName()), "name", query.getName())
                .eq(StringUtils.isNotBlank(query.getParentId()), "parent_id", query.getParentId())
                .eq(null != query.getHide(), "hide", query.getHide())
                .in(CollectionUtils.isNotEmpty(query.getIds()), "id", query.getIds())
                .eq(StringUtils.isNotBlank(query.getAppId()), "app_id", query.getAppId())
                .orderByAsc("sort");
    }

    /**
     * 查找子菜单
     *
     * @param parentId 父菜单id
     * @return 子菜单列表
     */
    @Override
    public List<MenuDTO> findByParent(String appId, String parentId) {
        return this.query(MenuQuery.builder().appId(appId).parentId(parentId).build());
    }

    /**
     * 插入前处理
     * 可以执行相关参数校验，校验不通过时抛出异常
     *
     * @param dto 数据对象
     * @return false时不进行插入，true时进行插入
     */
    @Override
    protected boolean processBeforeInsert(MenuDTO dto) {
        // 同一主菜单下子菜单名称不能重复；
        String name = dto.getName();
        String appId = Optional.ofNullable(dto.getAppId()).orElse("sys");
        dto.setAppId(appId);
        List<MenuDTO> menus = this.findByParent(appId, dto.getParentId());
        if (CollectionUtils.isNotEmpty(menus)) {
            if (menus.stream().anyMatch(item -> item.getName().equals(name))) {
                throw AppException.of(BASE_MENU_NAME_EXISTS);
            }
        }

        return super.processBeforeInsert(dto);
    }

    /**
     * 获取树形结构
     *
     * @param withHide    是否展示隐藏菜单，在配置过程中需要使用
     * @param withButtons 是否包含按钮
     * @return 树形菜单
     */
    @Override
    public List<MenuDTO> getTree(String appId, Boolean withHide, Boolean withButtons) {
        // 获取用户有权限的菜单与按钮信息
        UserContext userContext = UserContextHolder.get().orElse(null);
        if (null == userContext) {
            return new ArrayList<>(0);
        }

        List<String> userResourceIds = null;
        if (!userContext.getIsSuperAdmin()) {
            userResourceIds = userService.getUserResourceIds(userContext.getUserId());
            if (CollectionUtils.isEmpty(userResourceIds)) {
                return new ArrayList<>(0);
            }
        }

        MenuQuery query = MenuQuery.builder()
                .hide(!withHide ? false : null)
                .appId(appId)
                .build();

        if (null != userResourceIds) {
            query.setIds(userResourceIds);
        }

        List<MenuDTO> menus = this.query(query);
        if (CollectionUtils.isEmpty(menus)) {
            return new ArrayList<>(0);
        }

        // 补充按钮
        if (null != withButtons && withButtons) {
            List<MenuButtonDTO> buttons = menuButtonService.findAll();
            if (!CollectionUtils.isEmpty(buttons)) {
                // 将按钮按菜单进行归并
                List<String> pUserResourceIds = userResourceIds;
                Map<String, List<MenuButtonDTO>>  menuButtonMap = ListEnhancer.of(buttons)
                        .filter(button -> {
                            String id = button.getId();
                            if (null == pUserResourceIds) {
                                return true;
                            }

                            return pUserResourceIds.contains(id);
                        })
                        .toListMap(item -> new BiValue<>(item.getMenuId(), item));

                menus.forEach(menu -> menu.setButtons(menuButtonMap.getOrDefault(menu.getId(), new ArrayList<>(0))));
            }
        }

        return TreeUtils.toTree(menus);
    }
}
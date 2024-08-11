package com.liuqi.base.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.liuqi.base.domain.mapper.MenuMapper;
import com.liuqi.base.service.MenuService;
import com.liuqi.base.bean.dto.MenuDTO;
import com.liuqi.base.bean.query.MenuQuery;
import com.liuqi.base.domain.entity.MenuEntity;
import com.liuqi.common.base.bean.dto.Tree;
import com.liuqi.common.base.service.AbstractBaseService;
import com.liuqi.common.exception.AppException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.liuqi.common.ErrorCodes.BASE_MENU_CODE_EXISTS;
import static com.liuqi.common.ErrorCodes.BASE_MENU_NAME_EXISTS;

@Service
public class MenuServiceImpl extends AbstractBaseService<MenuEntity, MenuDTO, MenuMapper, MenuQuery> implements MenuService {
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
                .eq(StringUtils.isNotBlank(query.getParentId()), "parent_id", query.getParentId());
    }

    private Optional<MenuDTO> findByCode(String code) {
        return this.findOne(MenuQuery.builder().code(code).build());
    }

    /**
     * 查找子菜单
     *
     * @param parentId 父菜单id
     * @return 子菜单列表
     */
    @Override
    public List<MenuDTO> findByParent(String parentId) {
        return this.query(MenuQuery.builder().parentId(parentId).build());
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
        // 菜单编码全局不能重复
        String code = dto.getCode();
        if (StringUtils.isNotBlank(code)) {
            if (this.findByCode(code).isPresent()) {
                throw AppException.of(BASE_MENU_CODE_EXISTS);
            }
        }

        // 同一主菜单下子菜单名称不能重复；
        String name = dto.getName();
        List<MenuDTO> menus = this.findByParent(dto.getParentId());
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
     * @return 树形菜单
     */
    @Override
    public List<Tree<MenuDTO>> getTree() {
        List<MenuDTO> menus = this.findAll();
        if (CollectionUtils.isEmpty(menus)) {
            return new ArrayList<>(0);
        }

        // 根据parentId进行归并
        List<Tree<MenuDTO>> items = new ArrayList<>(16);
        Map<String, List<Tree<MenuDTO>>> childrenMap = menus.stream()
                .map(d -> Tree.<MenuDTO>builder().id(d.getId()).label(d.getName()).data(d).build())
                .peek(items::add)
                .collect(Collectors.groupingBy(d -> d.getData().getParentId()));

        // 设置每个元素的子元素
        items.forEach(item -> {
            List<Tree<MenuDTO>> children = Optional.ofNullable(childrenMap.get(item.getId()))
                    .orElse(new ArrayList<>(0));
            item.setChildren(children);
        });

        // 取顶层元素的子元素
        return Optional.ofNullable(childrenMap.get("-1"))
                .orElse(new ArrayList<>(0));
    }
}
package com.liuqi.acode.base.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.liuqi.acode.base.bean.dto.DeptDTO;
import com.liuqi.acode.base.bean.query.DeptQuery;
import com.liuqi.acode.base.common.ErrorCodes;
import com.liuqi.acode.base.domain.entity.DeptEntity;
import com.liuqi.acode.base.domain.mapper.DeptMapper;
import com.liuqi.acode.base.service.DeptService;
import com.liuqi.acode.base.service.DeptUserService;
import com.liuqi.common.base.bean.dto.Tree;
import com.liuqi.common.base.service.AbstractBaseService;
import com.liuqi.common.exception.AppException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 机构服务实现
 *
 * @author Coder Generator 2023-12-19 19:09:28
 **/
@Service
public class DeptServiceImpl extends AbstractBaseService<DeptEntity, DeptDTO, DeptMapper, DeptQuery> implements DeptService {
    @Autowired
    private DeptUserService deptUserService;

    @Override
    public DeptDTO toDTO(DeptEntity entity) {
        DeptDTO dto = new DeptDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    @Override
    public DeptEntity toEntity(DeptDTO dto) {
        DeptEntity entity = new DeptEntity();
        BeanUtils.copyProperties(dto, entity);
        return entity;
    }

    @Override
    protected QueryWrapper<DeptEntity> queryToWrapper(DeptQuery query) {
        return this.createQueryWrapper()
                .eq(StringUtils.isNotBlank(query.getCode()), "code", query.getCode())
                .eq(StringUtils.isNotBlank(query.getName()), "name", query.getName())
                .eq(StringUtils.isNotBlank(query.getParentId()), "parent_id", query.getParentId())
                .in(CollectionUtils.isNotEmpty(query.getParentIds()), "parent_id", query.getParentId());
    }

    /**
     * 通过编码查找机构
     *
     * @param code 机构编码
     * @return 机构信息
     */
    @Override
    public Optional<DeptDTO> findByCode(String code) {
        return this.findOne(DeptQuery.builder().code(code).build());
    }

    /**
     * 根据父级id及名称查找机构
     *
     * @param parentId 父级机构id
     * @param name     机构名称
     * @return 查找到的记录
     */
    public Optional<DeptDTO> findByParentAndName(String parentId, String name) {
        return this.findOne(DeptQuery.builder().name(name).parentId(parentId).build());
    }

    /**
     * 查询子级机构
     *
     * @param parentId 父级id
     * @return 子机构列表
     */
    public List<DeptDTO> findByParent(String parentId) {
        return this.query(DeptQuery.builder().parentId(parentId).build());
    }

    /**
     * 批量查询子机构
     *
     * @param parentIds 父级机构id
     * @return 子机构列表
     */
    public Map<String, List<DeptDTO>> findByParents(Collection<String> parentIds) {
        return this.query(DeptQuery.builder().parentIds(parentIds).build())
                .stream()
                .collect(Collectors.groupingBy(DeptDTO::getParentId));
    }

    /**
     * 插入前处理
     * 可以执行相关参数校验，校验不通过时抛出异常
     *
     * @param dto 机构信息
     * @return false时不进行插入，true时进行插入
     */
    @Override
    protected boolean processBeforeInsert(DeptDTO dto) {
        // 机构编码不能重复
        String code = dto.getCode();
        this.findByCode(code)
                .ifPresent(t -> {
                    throw AppException.of(ErrorCodes.BASE_DEPT_CODE_EXISTS);
                });

        // 同一父机构下机构名称不能重复
        if (StringUtils.isBlank(dto.getParentId())) {
            dto.setParentId("-1");
        } else {
            this.findByParentAndName(dto.getParentId(), dto.getName())
                    .ifPresent(t -> {
                        throw AppException.of(ErrorCodes.BASE_DEPT_NAME_DUPLICATED_IN_PARENT);
                    });
        }

        // 检查父机构是否存在
        String parentId = dto.getParentId();
        if (StringUtils.isNotBlank(parentId) && !"-1".equals(parentId)) {
            if (Optional.ofNullable(this.getById(parentId)).isEmpty()) {
                throw AppException.of(ErrorCodes.BASE_DEPT_NOT_EXISTS);
            }
        }

        return true;
    }

    /**
     * 更新前处理
     *
     * @param dto 更新对象
     * @return false时不再进行更新，true时进行更新
     */
    @Override
    protected boolean processBeforeUpdate(DeptDTO dto) {
        // 如果修改机构编码，那么不能与其它机构的编码重复
        String code = dto.getCode();
        if (StringUtils.isNotBlank(code)) {
            this.findByCode(code).ifPresent(item -> {
                if (item.getId().equals(dto.getId())) {
                    return;
                }

                throw AppException.of(ErrorCodes.BASE_DEPT_CODE_EXISTS);
            });
        }

        // 如果修改名称，名称在同一父机构下不能重复
        String parentId = dto.getParentId();
        if (StringUtils.isBlank(parentId)) {
            DeptEntity entity = this.getById(dto.getId());
            if (null == entity) {
                throw AppException.of(ErrorCodes.BASE_DEPT_NOT_EXISTS);
            }

            parentId = entity.getParentId();
        } else if (!"-1".equals(parentId)){
            // 如果修改父机构，父机构必须存在
            if (Optional.ofNullable(this.getById(parentId)).isEmpty()) {
                throw AppException.of(ErrorCodes.BASE_DEPT_NOT_EXISTS);
            }
        }

        if (StringUtils.isNotBlank(dto.getName())) {
            boolean sameNameExists = this.findByParent(parentId).stream()
                    .anyMatch(d -> d.getName().equals(dto.getName()) && !d.getId().equals(dto.getId()));
            if (sameNameExists) {
                throw AppException.of(ErrorCodes.BASE_DEPT_NAME_DUPLICATED_IN_PARENT);
            }
        }

        return super.processBeforeUpdate(dto);
    }

    /**
     * 删除前处理
     *
     * @param ids 批量删除机构
     * @return true时进行删除，false时不进行删除
     */
    @Override
    protected boolean processBeforeDelete(Collection<String> ids) {
        // 删除时需要检查是否有子机构
        Map<String, List<DeptDTO>> parentMap = this.findByParents(ids);
        if (!parentMap.isEmpty()) {
            throw AppException.of(ErrorCodes.BASE_DEPT_HAS_CHIlDREN);
        }

        return super.processBeforeDelete(ids);
    }

    /**
     * 删除后处理
     *
     * @param ids 机构id列表
     */
    @Override
    protected void processAfterDelete(Collection<String> ids) {
        // 删除机构时需要删除机构与用户关系
        deptUserService.deleteByDept(ids);

        super.processAfterDelete(ids);
    }

    /**
     * 获取机构树
     *
     * @return 机构树
     */
    @Override
    public List<Tree<DeptDTO>> getTree() {
        List<DeptDTO> list = this.findAll();
        if (CollectionUtils.isEmpty(list)) {
            return new ArrayList<>(0);
        }

        // 根据parentId进行归并
        List<Tree<DeptDTO>> items = new ArrayList<>(16);
        Map<String, List<Tree<DeptDTO>>> childrenMap = list.stream()
                .map(d -> Tree.<DeptDTO>builder().data(d).id(d.getId()).label(d.getName()).build())
                .peek(items::add)
                .collect(Collectors.groupingBy(t -> t.getData().getParentId()));

        // 设置每一项的子元素
        items.forEach(item -> {
            String id = item.getId();
            List<Tree<DeptDTO>> children = Optional.ofNullable(childrenMap.get(id)).orElse(new ArrayList<>(0));
            item.setChildren(children);
        });


        // 取父元素为顶层元素的列表（即父元素为-1的列表）
        return Optional.ofNullable(childrenMap.get("-1")).orElse(new ArrayList<>(0));
    }
}
package com.liuqi.acode.base.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.liuqi.acode.base.bean.dto.RoleDTO;
import com.liuqi.acode.base.bean.query.RoleQuery;
import com.liuqi.acode.base.domain.entity.RoleEntity;
import com.liuqi.acode.base.domain.mapper.RoleMapper;
import com.liuqi.acode.base.service.UserRoleService;
import com.liuqi.acode.base.common.ErrorCodes;
import com.liuqi.acode.base.service.RoleService;
import com.liuqi.common.base.service.AbstractBaseService;
import com.liuqi.common.exception.AppException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
public class RoleServiceImpl extends AbstractBaseService<RoleEntity, RoleDTO, RoleMapper, RoleQuery> implements RoleService {
    @Autowired
    private UserRoleService userRoleService;

    @Override
    public RoleDTO toDTO(RoleEntity entity) {
        RoleDTO dto = RoleDTO.builder().build();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    @Override
    public RoleEntity toEntity(RoleDTO dto) {
        RoleEntity entity = RoleEntity.builder().build();
        BeanUtils.copyProperties(dto, entity);
        return entity;
    }

    @Override
    protected QueryWrapper<RoleEntity> queryToWrapper(RoleQuery userQuery) {
        return this.createQueryWrapper()
                .eq(StringUtils.isNotBlank(userQuery.getCode()), "code", userQuery.getCode())
                .eq(StringUtils.isNotBlank(userQuery.getName()), "name", userQuery.getName());
    }

    /**
     * 插入前处理
     * 可以执行相关参数校验，校验不通过时抛出异常
     *
     * @param dto 角色
     * @return false时不进行插入，true时进行插入
     */
    @Override
    protected boolean processBeforeInsert(RoleDTO dto) {
        // 角色编码与名称不能重复
        List<RoleEntity> entities = this.findByCodeOrName(dto.getCode(), dto.getName());
        if (!entities.isEmpty()) {
            throw AppException.of(ErrorCodes.BASE_ROLE_EXISTS);
        }

        return super.processBeforeInsert(dto);
    }

    /**
     * 查找编码或者名称为指定值的记录
     *
     * @param code 角色编码
     * @param name 角色名称
     * @return 满足条件的记录
     */
    private List<RoleEntity> findByCodeOrName(String code, String name) {
        QueryWrapper<RoleEntity> queryWrapper = this.createQueryWrapper()
                .eq("code", code)
                .or(q -> q.eq("name", name));
        return this.list(queryWrapper);
    }

    /**
     * 根据编码查询角色信息
     *
     * @param code 角色编码
     * @return 角色信息
     */
    private Optional<RoleDTO> findByCode(String code) {
        return this.findOne(RoleQuery.builder().code(code).build());
    }

    /**
     * 根据名称查找角色
     *
     * @param name 名称
     * @return 角色信息
     */
    private Optional<RoleDTO> findByName(String name) {
        return this.findOne(RoleQuery.builder().name(name).build());
    }

    /**
     * 更新前处理
     *
     * @param dto 角色信息
     * @return false时不再进行更新，true时进行更新
     */
    @Override
    protected boolean processBeforeUpdate(RoleDTO dto) {
        // 角色编码与名称不能重复
        String code = dto.getCode();
        String name = dto.getName();
        String id = dto.getId();

        if (StringUtils.isNotBlank(code)) {
            boolean exists = this.findByCode(code).filter(d -> !d.getId().equals(id)).isPresent();
            if (exists) {
                throw AppException.of(ErrorCodes.BASE_ROLE_CODE_EXISTS);
            }
        }

        if (StringUtils.isNotBlank(name)) {
            boolean exists = this.findByName(name).filter(d -> !d.getId().equals(id)).isPresent();
            if (exists) {
                throw AppException.of(ErrorCodes.BASE_ROLE_NAME_EXISTS);
            }
        }

        return super.processBeforeUpdate(dto);
    }

    /**
     * 删除后处理
     *
     * @param roleIds 角色id列表
     */
    @Override
    protected void processAfterDelete(Collection<String> roleIds) {
        // 删除角色时需要删除对应的用户角色信息
        userRoleService.deleteByRole(roleIds);

        super.processAfterDelete(roleIds);
    }
}

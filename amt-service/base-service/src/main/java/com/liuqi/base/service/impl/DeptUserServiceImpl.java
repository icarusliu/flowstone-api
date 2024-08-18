package com.liuqi.base.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.liuqi.base.bean.query.DeptUserQuery;
import com.liuqi.base.domain.mapper.DeptUserMapper;
import com.liuqi.base.service.DeptUserService;
import com.liuqi.base.bean.dto.DeptUserDTO;
import com.liuqi.base.domain.entity.DeptUserEntity;
import com.liuqi.common.base.service.AbstractBaseService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

/**
 * 机构用户关联服务
 */
@Service
public class DeptUserServiceImpl extends AbstractBaseService<DeptUserEntity, DeptUserDTO, DeptUserMapper, DeptUserQuery> implements DeptUserService {
    @Override
    public DeptUserDTO toDTO(DeptUserEntity entity) {
        DeptUserDTO dto = new DeptUserDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    @Override
    public DeptUserEntity toEntity(DeptUserDTO dto) {
        DeptUserEntity entity = new DeptUserEntity();
        BeanUtils.copyProperties(dto, entity);
        return entity;
    }

    @Override
    protected QueryWrapper<DeptUserEntity> queryToWrapper(DeptUserQuery query) {
        return this.createQueryWrapper();
    }

    /**
     * 根据用户删除对应关系
     *
     * @param userIds 用户id清单
     */
    @Override
    public void deleteByUser(Collection<String> userIds) {
        if (CollectionUtils.isEmpty(userIds)) {
            return;
        }
        this.remove(this.createQueryWrapper().in("user_id", userIds));
    }

    /**
     * 根据机构id列表删除关系
     *
     * @param deptIds 机构id列表
     */
    @Override
    public void deleteByDept(Collection<String> deptIds) {
        if (CollectionUtils.isEmpty(deptIds)) {
            return;
        }

        this.remove(this.createQueryWrapper().in("dept_id", deptIds));
    }

    /**
     * 插入前处理
     * 可以执行相关参数校验，校验不通过时抛出异常
     *
     * @param dto 插入对象
     * @return false时不进行插入，true时进行插入
     */
    @Override
    protected boolean processBeforeInsert(DeptUserDTO dto) {
        // 用户与机构关系如果已经存在，不需要重复添加
        String userId = dto.getUserId();
        String deptId = dto.getDeptId();
        if (StringUtils.isEmpty(userId) || StringUtils.isBlank(deptId)) {
            log.warn("Userid or deptid is null!");
            return false;
        }

        DeptUserQuery query = DeptUserQuery.builder()
                .userId(userId)
                .deptId(deptId)
                .build();
        long c = this.count(query);
        if (c > 0) {
            log.debug("Dept user has already existed!");
            return false;
        }

        return super.processBeforeInsert(dto);
    }

    /**
     * 删除机构与用户关系
     *
     * @param deptId  机构id
     * @param userIds 用户id列表
     */
    @Override
    public void deleteDeptUsers(String deptId, List<String> userIds) {
        if (StringUtils.isEmpty(deptId) || CollectionUtils.isEmpty(userIds)) {
            return;
        }

        this.remove(this.createQueryWrapper().eq("dept_id", deptId)
                .in("user_id", userIds));
    }
}
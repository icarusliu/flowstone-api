package com.liuqi.base.service;

import com.liuqi.base.bean.query.DeptUserQuery;
import com.liuqi.base.bean.dto.DeptUserDTO;
import com.liuqi.base.domain.entity.DeptUserEntity;
import com.liuqi.common.base.service.BaseService;

import java.util.Collection;
import java.util.List;

public interface DeptUserService extends BaseService<DeptUserDTO, DeptUserQuery> {
    /**
     * 根据用户删除对应关系
     *
     * @param userIds 用户id清单
     */
    void deleteByUser(Collection<String> userIds);

    /**
     * 根据机构id列表删除关系
     *
     * @param deptIds 机构id列表
     */
    void deleteByDept(Collection<String> deptIds);

    /**
     * 删除机构与用户关系
     *
     * @param deptId  机构id
     * @param userIds 用户id列表
     */
    void deleteDeptUsers(String deptId, List<String> userIds);
}
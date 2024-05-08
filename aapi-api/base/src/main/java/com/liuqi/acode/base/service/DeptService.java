package com.liuqi.acode.base.service;

import com.liuqi.acode.base.bean.dto.DeptDTO;
import com.liuqi.acode.base.bean.query.DeptQuery;
import com.liuqi.acode.base.domain.entity.DeptEntity;
import com.liuqi.common.base.bean.dto.Tree;
import com.liuqi.common.base.service.BaseService;

import java.util.List;
import java.util.Optional;

/**
 * 机构服务
 */
public interface DeptService extends BaseService<DeptEntity, DeptDTO, DeptQuery> {
    /**
     * 通过编码查找机构
     *
     * @param code 机构编码
     * @return 机构信息
     */
    Optional<DeptDTO> findByCode(String code);

    /**
     * 获取机构树
     *
     * @return 机构树
     */
    List<Tree<DeptDTO>> getTree();
}
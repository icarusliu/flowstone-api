package com.liuqi.base.service;

import com.liuqi.base.bean.dto.DeptDTO;
import com.liuqi.base.bean.query.DeptQuery;
import com.liuqi.common.base.service.BaseService;

import java.util.Optional;

/**
 * 机构服务
 */
public interface DeptService extends BaseService<DeptDTO, DeptQuery> {
    /**
     * 通过编码查找机构
     *
     * @param code 机构编码
     * @return 机构信息
     */
    Optional<DeptDTO> findByCode(String code);

}
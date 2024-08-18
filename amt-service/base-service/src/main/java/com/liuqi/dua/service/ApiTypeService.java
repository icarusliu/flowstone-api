package com.liuqi.dua.service;

import com.liuqi.common.base.service.BaseService;
import com.liuqi.dua.bean.dto.ApiTypeDTO;
import com.liuqi.dua.bean.query.ApiTypeQuery;

import java.util.List;

/**
 * 接口分类服务接口
 *
 * @author Coder Generator 2024-08-09 22:08:31
 **/
public interface ApiTypeService extends BaseService<ApiTypeDTO, ApiTypeQuery> {
    /**
     * 获取分类树型结构
     *
     * @return 分类树型结构
     */
    List<ApiTypeDTO> tree();
}
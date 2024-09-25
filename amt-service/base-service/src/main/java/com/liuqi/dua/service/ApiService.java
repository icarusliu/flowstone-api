package com.liuqi.dua.service;

import com.liuqi.common.base.service.BaseService;
import com.liuqi.dua.bean.dto.ApiDTO;
import com.liuqi.dua.bean.query.ApiQuery;

import java.util.Optional;

/**
 * 接口服务接口
 *
 * @author Coder Generator 2024-07-08 22:32:36
 **/
public interface ApiService extends BaseService<ApiDTO, ApiQuery> {
    /**
     * 通过路径查找接口
     * @param path 路径
     * @return 接口信息
     */
    Optional<ApiDTO> findByPath(String path);
}
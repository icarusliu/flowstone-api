package com.liuqi.dua.service;

import com.liuqi.common.base.service.BaseService;
import com.liuqi.dua.bean.dto.ApiDraftDTO;
import com.liuqi.dua.bean.query.ApiDraftQuery;

import java.util.Optional;

/**
 * 接口草稿服务接口
 *
 * @author Coder Generator 2024-07-08 22:33:46
 **/
public interface ApiDraftService extends BaseService<ApiDraftDTO, ApiDraftQuery> {
    /**
     * 根据路径查找接口
     *
     * @param path 接口路径
     * @return 接口
     */
    default Optional<ApiDraftDTO> findByPath(String path) {
        ApiDraftQuery query = new ApiDraftQuery();
        query.setPath(path);
        return this.query(query).stream().findAny();
    }

    /**
     * 发布接口
     *
     * @param id 接口id
     */
    void publish(String id);

    /**
     * 接口下线
     *
     * @param id 接口id
     */
    void offline(String id);
}
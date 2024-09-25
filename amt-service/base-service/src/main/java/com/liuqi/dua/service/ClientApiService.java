package com.liuqi.dua.service;

import com.liuqi.common.base.service.BaseService;
import com.liuqi.dua.bean.dto.ClientApiDTO;
import com.liuqi.dua.bean.query.ClientApiQuery;

import java.util.List;

/**
 * 客户端接口列表服务接口 
 * @author Coder Generator 2024-09-25 15:58:18 
 **/
public interface ClientApiService extends BaseService<ClientApiDTO, ClientApiQuery> {
    default List<ClientApiDTO> findByClient(String clientId) {
        ClientApiQuery query = new ClientApiQuery();
        query.setClientId(clientId);
        return this.query(query);
    }

    /**
     * 删除客户端与接口绑定关系
     * @param clientId 客户端id
     * @param apiIds 接口id列表
     */
    void delete(String clientId, List<String> apiIds);
}
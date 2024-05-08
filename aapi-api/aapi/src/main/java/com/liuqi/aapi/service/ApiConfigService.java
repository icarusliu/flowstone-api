package com.liuqi.aapi.service;

import com.liuqi.aapi.bean.dto.ApiConfigDTO;
import com.liuqi.aapi.bean.query.ApiConfigQuery;
import com.liuqi.aapi.domain.entity.ApiConfigEntity;
import com.liuqi.common.base.service.BaseService;

import java.util.Optional;

/** API配置表服务接口 
 * @author Coder Generator 2024-04-18 14:25:01 **/
public interface ApiConfigService extends BaseService<ApiConfigDTO, ApiConfigQuery> {
    Optional<ApiConfigDTO> findByPath(String tenantId, String path);
}
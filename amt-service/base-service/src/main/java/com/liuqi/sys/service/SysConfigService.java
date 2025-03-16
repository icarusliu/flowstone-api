package com.liuqi.sys.service;

import com.liuqi.sys.bean.dto.SysConfigDTO;
import com.liuqi.sys.bean.query.SysConfigQuery;
import com.liuqi.common.base.service.BaseService;

import java.util.Optional;

public interface SysConfigService extends BaseService<SysConfigDTO, SysConfigQuery> {
    /**
     * 根据编码查找配置项
     *
     * @param code 配置项编码
     * @return 配置项，不存在时返回空
     */
    Optional<SysConfigDTO> findByCode(String code);
}
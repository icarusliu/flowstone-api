package com.liuqi.base.service;

import com.liuqi.base.bean.dto.SysConfigDTO;
import com.liuqi.base.bean.query.SysConfigQuery;
import com.liuqi.base.domain.entity.SysConfigEntity;
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
package com.liuqi.acode.base.service;

import com.liuqi.acode.base.bean.dto.DictDTO;
import com.liuqi.acode.base.bean.query.DictQuery;
import com.liuqi.acode.base.domain.entity.DictEntity;
import com.liuqi.common.base.service.BaseService;

import java.util.Optional;

public interface DictService extends BaseService<DictEntity, DictDTO, DictQuery> {
    Optional<DictDTO> findByCode(String code);
}
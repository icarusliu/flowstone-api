package com.liuqi.base.service;

import com.liuqi.base.bean.dto.DictDTO;
import com.liuqi.base.bean.query.DictQuery;
import com.liuqi.base.domain.entity.DictEntity;
import com.liuqi.common.base.service.BaseService;

import java.util.Optional;

public interface DictService extends BaseService<DictDTO, DictQuery> {
    Optional<DictDTO> findByCode(String code);
}
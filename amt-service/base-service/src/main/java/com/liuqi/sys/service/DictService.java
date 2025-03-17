package com.liuqi.sys.service;

import com.liuqi.sys.bean.dto.DictDTO;
import com.liuqi.sys.bean.query.DictQuery;
import com.liuqi.common.base.service.BaseService;

import java.util.List;
import java.util.Optional;

public interface DictService extends BaseService<DictDTO, DictQuery> {
    Optional<DictDTO> findByCode(String code);

    /**
     * 查字典明细
     * @param codes 字典编码列表
     * @return 字典项明细
     */
    List<DictDTO> findByCodes(List<String> codes);
}
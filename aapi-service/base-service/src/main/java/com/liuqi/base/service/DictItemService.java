package com.liuqi.base.service;

import com.liuqi.base.bean.dto.DictItemDTO;
import com.liuqi.base.bean.query.DictItemQuery;
import com.liuqi.base.domain.entity.DictItemEntity;
import com.liuqi.common.base.service.BaseService;

import java.util.Collection;

public interface DictItemService extends BaseService<DictItemDTO, DictItemQuery> {
    /**
     * 根据字典id列表删除字典项
     *
     * @param dictIds 字典id列表
     */
    void deleteByDicts(Collection<String> dictIds);

    /**
     * 查找字典对应的字典项清单
     *
     * @param dictId 字典id
     * @return 字典项清单
     */
    Collection<DictItemDTO> findByDict(String dictId);
}
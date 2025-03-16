package com.liuqi.base.service;

import com.liuqi.common.base.service.BaseService;
import com.liuqi.base.bean.dto.ModelFieldDTO;
import com.liuqi.base.bean.query.ModelFieldQuery;

import java.util.List;

/**
 * 模型字段服务接口 
 * @author Coder Generator 2025-03-14 12:59:51 
 **/
public interface ModelFieldService extends BaseService<ModelFieldDTO, ModelFieldQuery> {
    /**
     * 删除不在指定范围内的字段
     * @param modelId 模型id
     * @param fieldIds 不能删除的字段id，其它字段都删除
     */
    void deleteModelFieldsNotIn(String modelId, List<String> fieldIds);

    /**
     * 根据模型查找字段列表
     * @param modelId 模型id
     * @return 模型对应字段列表
     */
    default List<ModelFieldDTO> findByModel(String modelId) {
        ModelFieldQuery q = new ModelFieldQuery();
        q.setModelId(modelId);
        return this.query(q);
    }
}
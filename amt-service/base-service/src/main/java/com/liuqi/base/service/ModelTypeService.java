package com.liuqi.base.service;

import com.liuqi.common.base.service.BaseService;
import com.liuqi.base.bean.dto.ModelTypeDTO;
import com.liuqi.base.bean.query.ModelTypeQuery;

import java.util.List;

/**
 * 模型分类服务接口 
 * @author Coder Generator 2025-03-14 12:16:29 
 **/
public interface ModelTypeService extends BaseService<ModelTypeDTO, ModelTypeQuery> {
    /**
     * 获取树形结构
     */
    List<ModelTypeDTO> tree();
}
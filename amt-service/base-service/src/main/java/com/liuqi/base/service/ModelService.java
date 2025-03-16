package com.liuqi.base.service;

import com.liuqi.common.base.service.BaseService;
import com.liuqi.base.bean.dto.ModelDTO;
import com.liuqi.base.bean.dto.ModelDetailDTO;
import com.liuqi.base.bean.query.ModelQuery;
import com.liuqi.base.bean.req.ModelAddReq;
import com.liuqi.base.bean.req.ModelUpdateReq;

/**
 * 模型服务接口 
 * @author Coder Generator 2025-03-14 12:45:23 
 **/
public interface ModelService extends BaseService<ModelDTO, ModelQuery> {
    /**
     * 新增模型
     * @param req 请求参数
     */
    ModelDTO insert(ModelAddReq req);

    /**
     * 模型修改
     * @param req 请求参数
     */
    void update(ModelUpdateReq req);

    /**
     * 模型下线
     * @param id 模型id
     */
    void offline(String id);

    /**
     * 模型发布
     * @param id 模型id
     */
    void publish(String id);

    /**
     * 获取模型详情，包含有模型配置信息
     * @param id 模型id
     * @return 模型详情信息
     */
    ModelDetailDTO getDetail(String id);
}
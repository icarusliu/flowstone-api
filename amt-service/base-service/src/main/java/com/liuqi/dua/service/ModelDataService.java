package com.liuqi.dua.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.liuqi.common.ErrorCodes;
import com.liuqi.common.base.bean.query.DynamicQuery;
import com.liuqi.common.exception.AppException;
import com.liuqi.common.utils.DynamicSqlHelper;
import com.liuqi.dua.bean.dto.ModelDTO;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 模型数据服务
 *
 * @author LiuQi 2025/3/14-21:34
 * @version V1.0
 **/
@Service
public class ModelDataService {
    @Autowired
    private ModelService modelService;

    @Autowired
    private ModelFieldService modelFieldService;

    /**
     * 模型数据查询
     * @param modelId 模型id
     * @param query 查询条件
     * @return 查询结果
     */
    public IPage<Map<String, Object>> pageQuery(String modelId, DynamicQuery query) {
        // 查询模型信息
        ModelDTO model = modelService.findById(modelId).orElseThrow(AppException.supplier(ErrorCodes.DUA_MODEL_NOT_EXISTS));
        String tableName = model.getTableName();
        String sql = "select * from " + tableName;
        Pair<String, Map<String, Object>> whereSql = query.toWhereSql();
        Map<String, Object> params = whereSql.getValue();
        if (StringUtils.isNotBlank(whereSql.getKey())) {
            sql += " where " + whereSql.getKey();
        }

        if (null != query.getPageNo() && null != query.getPageSize()) {
            params.put("pageNo", query.getPageNo());
            params.put("pageSize", query.getPageSize());
        }

        return (IPage<Map<String, Object>>) DynamicSqlHelper.executeSql("model-query-" + modelId, sql, params);
    }

    /**
     * 新增数据
     * @param modelId 模型id
     * @param body 新增数据内容
     * @return 新增结果，带主键信息
     */
    public Map<String, Object> save(String modelId, Map<String, Object> body) {
        ModelDTO model = modelService.findById(modelId).orElseThrow(AppException.supplier(ErrorCodes.DUA_MODEL_NOT_EXISTS));
        String tableName = model.getTableName();

        return null;
    }
}

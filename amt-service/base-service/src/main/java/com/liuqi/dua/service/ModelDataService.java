package com.liuqi.dua.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.liuqi.common.ErrorCodes;
import com.liuqi.common.base.bean.query.DynamicQuery;
import com.liuqi.common.bean.UserContextHolder;
import com.liuqi.common.exception.AppException;
import com.liuqi.common.utils.DynamicSqlHelper;
import com.liuqi.common.utils.value.Value;
import com.liuqi.dua.bean.dto.ModelFieldDTO;
import com.liuqi.dua.bean.dto.ModelPublishedDTO;
import liquibase.util.StringUtil;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 模型数据服务
 *
 * @author LiuQi 2025/3/14-21:34
 * @version V1.0
 **/
@Service
public class ModelDataService {
    @Autowired
    private ModelPublishedService modelPublishedService;

    /**
     * 模型数据查询
     * @param modelId 模型id
     * @param query 查询条件
     * @return 查询结果
     */
    public IPage<Map<String, Object>> pageQuery(String modelId, DynamicQuery query) {
        // 查询模型信息
        ModelPublishedDTO model = modelPublishedService.findById(modelId).orElseThrow(AppException.supplier(ErrorCodes.DUA_MODEL_NOT_PUBLISHED));
        String tableName = model.getTableName();
        String sql = "select * from " + tableName;
        Pair<String, Map<String, Object>> whereSql = query.toWhereSql();
        Map<String, Object> params = whereSql.getValue();
        if (StringUtils.isNotBlank(whereSql.getKey())) {
            sql += " where " + whereSql.getKey();
        }

        // 处理排序语句
        List<Map<String, Object>> fields = model.getListFields();
        Value<Boolean> updateTimeExists = new Value<>();
        updateTimeExists.setValue(false);
        String orderBySql = fields.stream()
                .peek(item -> {
                    String code = MapUtils.getString(item, "code");
                    if (code.equals("updateTime")) {
                        updateTimeExists.setValue(true);
                    }
                })
                .filter(map -> MapUtils.getBooleanValue(map, "sort"))
                .map(m -> {
                    String sortType = MapUtils.getString(m, "sortType", "asc");
                    String code = MapUtils.getString(m, "code");
                    String column = StringUtil.toKabobCase(code).replaceAll("-", "_");
                    return column + " " + sortType;
                })
                .collect(Collectors.joining(","));

        // 如果没有排序，则使用更新时间进行排序
        if (StringUtils.isBlank(orderBySql) && updateTimeExists.isPresent()) {
            orderBySql = " update_time desc";
        }

        sql += " order by " + orderBySql;

        if (null != query.getPageNo() && null != query.getPageSize()) {
            params.put("pageNo", query.getPageNo());
            params.put("pageSize", query.getPageSize());
        }

        return (IPage<Map<String, Object>>) DynamicSqlHelper.executeSql("model-data-query-" + modelId, sql, params);
    }

    /**
     * 新增数据
     * @param modelId 模型id
     * @param body 新增数据内容
     * @return 新增结果，带主键信息
     */
    public Map<String, Object> save(String modelId, Map<String, Object> body) {
        ModelPublishedDTO model = modelPublishedService.findById(modelId).orElseThrow(AppException.supplier(ErrorCodes.DUA_MODEL_NOT_EXISTS));
        String tableName = model.getTableName();
        List<ModelFieldDTO> fields = model.getFields();

        // 组装插入语句
        List<String> insertFields = new ArrayList<>(16);
        List<String> valueFields = new ArrayList<>(16);

        // 获取主键信息
        ModelFieldDTO primaryField = fields.stream()
                .filter(ModelFieldDTO::getPrimaryKey)
                .findAny()
                .orElseThrow(AppException.supplier(ErrorCodes.DUA_MODEL_PRIMARY_KEY_NOT_EXISTS));

        String primaryCode = primaryField.getCode();
        String id = MapUtils.getString(body, primaryCode);
        if (StringUtils.isBlank(id)) {
            // 插入
            // 新增用户信息
            UserContextHolder.getUserId().ifPresent(userId -> body.put("createUser", userId));

            fields.forEach(field -> {
                if (field.getPrimaryKey() && field.getDataType().equals("varchar")) {
                    // 主键处理
                    insertFields.add(field.getColumnName());
                    valueFields.add(field.getCode());
                    body.put(field.getCode(), UUID.randomUUID().toString().replace("-", ""));
                    return;
                }

                Object value = body.get(field.getCode());
                if (null == value) {
                    return;
                }

                insertFields.add(field.getColumnName());
                valueFields.add(field.getCode());
            });

            String fieldSql = String.join(",", insertFields);
            String valueSql = valueFields.stream()
                    .map(item -> "#{" + item + "}")
                    .collect(Collectors.joining(","));
            String sql = "insert into " + tableName + "(" + fieldSql + ") values(" + valueSql + ")";
            DynamicSqlHelper.executeSql("model-data-insert-" + modelId, sql, body);
            return body;

        }

        // 更新
        // 新增用户信息
        UserContextHolder.getUserId().ifPresent(userId -> body.put("updateUser", userId));

        List<ModelFieldDTO> updateFields = new ArrayList<>(16);
        fields.forEach(field -> {
            if (field.getPrimaryKey()) {
                // 主键不处理
                return;
            }

            Object value = body.get(field.getCode());
            if (null == value) {
                return;
            }

            updateFields.add(field);
        });

        String fieldSql = updateFields.stream()
                .map(field -> field.getColumnName() + " = #{" + field.getCode() + "}")
                .collect(Collectors.joining(","));
        String sql = "update " + tableName + " set " + fieldSql + " where " + primaryField.getColumnName() + " = #{" + primaryField.getCode() + "}";
        DynamicSqlHelper.executeSql("model-data-update-" + modelId, sql, body);

        return body;
    }

    /**
     * 删除记录
     * @param modelId 模型id
     * @param params 参数
     */
    public void delete(String modelId, Map<String, Object> params) {
        // 从参数中取主键进行删除
        ModelPublishedDTO model = modelPublishedService.findById(modelId).orElseThrow(AppException.supplier(ErrorCodes.DUA_MODEL_NOT_EXISTS));
        String tableName = model.getTableName();
        List<ModelFieldDTO> fields = model.getFields();
        for (ModelFieldDTO field : fields) {
            if (field.getPrimaryKey()) {
                String sql = "delete from " + tableName + " where " + field.getColumnName() + " = #{" + field.getCode() + "}";
                DynamicSqlHelper.executeSql("model-delete-" + modelId, sql, params);
                return;
            }
        }

        throw AppException.of(ErrorCodes.DUA_MODEL_PRIMARY_KEY_NOT_EXISTS);
    }
}

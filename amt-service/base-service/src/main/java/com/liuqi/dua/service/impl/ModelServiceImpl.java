package com.liuqi.dua.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.liuqi.common.ErrorCodes;
import com.liuqi.common.base.service.AbstractBaseService;
import com.liuqi.common.exception.AppException;
import com.liuqi.common.utils.DynamicSqlHelper;
import com.liuqi.dua.bean.dto.ModelDTO;
import com.liuqi.dua.bean.dto.ModelFieldDTO;
import com.liuqi.dua.bean.dto.ModelPublishedDTO;
import com.liuqi.dua.bean.query.ModelQuery;
import com.liuqi.dua.bean.req.ModelAddReq;
import com.liuqi.dua.bean.req.ModelFieldAddReq;
import com.liuqi.dua.bean.req.ModelFieldUpdateReq;
import com.liuqi.dua.bean.req.ModelUpdateReq;
import com.liuqi.dua.domain.entity.ModelEntity;
import com.liuqi.dua.domain.mapper.ModelMapper;
import com.liuqi.dua.service.ModelFieldService;
import com.liuqi.dua.service.ModelPublishedService;
import com.liuqi.dua.service.ModelService;
import org.apache.catalina.users.SparseUserDatabase;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * 模型服务实现 
 * @author Coder Generator 2025-03-14 12:45:23 
 **/
@Service
public class ModelServiceImpl extends AbstractBaseService<ModelEntity, ModelDTO, ModelMapper, ModelQuery> implements ModelService {
    @Autowired
    private ModelFieldService modelFieldService;

    @Autowired
    private ModelPublishedService publishedService;

    private static final List<String> innerFields = Arrays.asList("id", "createTime", "createUser", "updateTime", "updateUser");

    @Override
    public ModelDTO toDTO(ModelEntity entity) {
        ModelDTO dto = new ModelDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    @Override
    public ModelEntity toEntity(ModelDTO dto) {
        ModelEntity entity = new ModelEntity();
        BeanUtils.copyProperties(dto, entity);
        return entity;
    }

    @Override
    protected QueryWrapper<ModelEntity> queryToWrapper(ModelQuery query) {
        return this.createQueryWrapper()
                .eq(StringUtils.isNotBlank(query.getId()), "id", query.getId())
                .in(null != query.getIds(), "id", query.getIds())
                .eq(StringUtils.isNotBlank(query.getCode()), "code", query.getCode())
                .eq(StringUtils.isNotBlank(query.getName()), "name", query.getName())
                .orderByDesc("create_time");
    }

    /**
     * 新增模型
     *
     * @param req 请求参数
     */
    @Override
    @Transactional
    public ModelDTO insert(ModelAddReq req) {
        // 判断模型是否存在
        ModelQuery query = new ModelQuery();
        query.setCode(req.getCode());
        if (this.count(query) > 0) {
            throw AppException.of(ErrorCodes.DUA_MODEL_CODE_EXISTS);
        }

        ModelDTO dto = new ModelDTO();
        BeanUtils.copyProperties(req, dto);

        // 判断对应的表是否存在
        checkTableExists(dto);

        dto = this.insert(dto);

        // 保存模型字段
        List<ModelFieldAddReq> fieldsReq = req.getFields();
        if (CollectionUtils.isEmpty(fieldsReq)) {
            return dto;
        }

        String modelId = dto.getId();
        List<ModelFieldDTO> fields = fieldsReq.stream()
                .map(f -> {
                    ModelFieldDTO field = new ModelFieldDTO();
                    BeanUtils.copyProperties(f, field);
                    field.setModelId(modelId);
                    return field;
                }).toList();

        modelFieldService.insert(fields);
        return dto;
    }

    /**
     * 校验表是否存在
     */
    private void checkTableExists(ModelDTO dto) {
        if (tableExists(dto)) {
            throw AppException.of(ErrorCodes.DUA_MODEL_TABLE_EXISTS);
        }
    }

    /**
     * 判断表是否存在
     * @param dto 模型对象
     * @return 表是否存在
     */
    private boolean tableExists(ModelDTO dto) {
        String tableName = dto.getTableName();
        String sql = "select count(1) as c from information_schema.tables where table_name = '" + tableName + "' and table_schema = database()";
        Object obj = DynamicSqlHelper.executeSql("model-test", sql, new HashMap<>(16));
        List<Map<String, Object>> list = (List<Map<String, Object>>) obj;
        if (!CollectionUtils.isEmpty(list)) {
            int c = MapUtils.getIntValue(list.get(0), "c");
            return c > 0;
        }

        return false;
    }

    /**
     * 模型修改
     *
     * @param req 请求参数
     */
    @Override
    @Transactional
    public void update(ModelUpdateReq req) {
        ModelDTO dto = new ModelDTO();
        BeanUtils.copyProperties(req, dto);
        int status = dto.getStatus();
        if (0 != status && 3 != status) {
            // 如果是已发布状态，将其设置成已修改
            dto.setStatus(2);
        }
        this.update(dto);

        // 更新模型字段列表
        List<ModelFieldUpdateReq> reqs = req.getFields();
        if (CollectionUtils.isEmpty(reqs)) {
            return;
        }

        List<String> fieldIds = new ArrayList<>(16);
        reqs.forEach(r -> {
            ModelFieldDTO field = new ModelFieldDTO();
            field.setModelId(dto.getId());
            BeanUtils.copyProperties(r, field);
            if (StringUtils.isBlank(field.getId())) {
                field = modelFieldService.insert(field);
            } else {
                modelFieldService.update(field);
            }

            fieldIds.add(field.getId());
        });

        // 删除字段
        modelFieldService.deleteModelFieldsNotIn(dto.getId(), fieldIds);
    }

    /**
     * 模型发布
     *
     * @param id 模型id
     */
    @Override
    @Transactional
    public void publish(String id) {
        ModelDTO model = this.findById(id).orElseThrow(AppException.supplier(ErrorCodes.DUA_MODEL_NOT_EXISTS));

        // 处理字段
        List<ModelFieldDTO> fields = modelFieldService.findByModel(id);
        if (CollectionUtils.isEmpty(fields)) {
            throw AppException.of(ErrorCodes.DUA_MODEL_FIELDS_EMPTY);
        }

        // 判断表是否存在，如果表存在时，进行修改，否则进行新增
        String tableName = model.getTableName();
        List<String> columns = this.getTableColumns(tableName);
        if (null != columns) {
            updateColumns(fields, columns, tableName);
        } else {
            // 表不存在，进行表新增
            StringBuilder sb = new StringBuilder()
                    .append("create table ")
                    .append(tableName)
                    .append("(");
            for (int i = 0; i < fields.size(); i++) {
                ModelFieldDTO field = fields.get(i);
                if (i > 0) {
                    sb.append(",");
                }
                sb.append(field.getCreateSql());
            }
            sb.append(") comment '")
                    .append(model.getName())
                    .append("'");
            DynamicSqlHelper.executeSql("create-table-" + tableName, sb.toString(), new HashMap<>(0));
        }

        model.setStatus(1);
        this.update(model);

        // 更新发布模型表
        ModelPublishedDTO publishedDTO = new ModelPublishedDTO();
        BeanUtils.copyProperties(model, publishedDTO);
        publishedDTO.setFields(fields);
        publishedService.deletePhysical(model.getId());
        publishedService.insert(publishedDTO);
    }

    /**
     * 表结构更新
     */
    private void updateColumns(List<ModelFieldDTO> fields, List<String> columns, String tableName) {
        // 表存在，进行表结构更新；表名不能动，所以能动的只是字段，包括几种情况：
        // 一是新增字段；二是修改原有字段；三是删除字段；
        StringBuilder sb = new StringBuilder();
        List<String> restColumns = new ArrayList<>(16);
        fields.forEach(field -> {
            // id及内置字段不能改
            String code = field.getCode();
            if (innerFields.contains(code)) {
                return;
            }

            String column = field.getColumnName();
            if (columns.contains(column)) {
                // 字段已存在，进行修改
                sb.append(field.getColumnUpdateSql(tableName, false));
            } else {
                // 字段不存在
                sb.append(field.getColumnUpdateSql(tableName, true));
            }

            restColumns.add(column);
        });

        columns.forEach(column -> {
            if (restColumns.contains(column)) {
                return;
            }

            sb.append("alter table ")
                    .append(tableName)
                    .append(" drop column ")
                    .append(column)
                    .append(";");
        });

        // 执行语句
        DynamicSqlHelper.executeSql("alter-table-" + tableName, sb.toString(), new HashMap<>(0));
    }

    /**
     * 查询表字段列表
     * @param tableName 表名
     * @return 表字段列表
     */
    private List<String> getTableColumns(String tableName) {
        String sql = "select column_name from information_schema.columns where TABLE_SCHEMA = database() and TABLE_NAME = '" + tableName + "'";
        List<Map<String, Object>> list = (List<Map<String, Object>>) DynamicSqlHelper.executeSql("table-names", sql, new HashMap<>(16));
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }

        return list.stream()
                .map(map -> MapUtils.getString(map, "columnName"))
                .toList();
    }

    /**
     * 模型下线
     *
     * @param id 模型id
     */
    @Override
    public void offline(String id) {
        ModelDTO dto = this.findById(id).orElseThrow(AppException.supplier(ErrorCodes.DUA_MODEL_NOT_EXISTS));
        dto.setStatus(3);
        this.update(dto);

        modelFieldService.deletePhysical(id);
    }
}
package com.liuqi.dua.service.impl;

import com.baomidou.dynamic.datasource.toolkit.DynamicDataSourceContextHolder;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.TableFieldInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.liuqi.common.ErrorCodes;
import com.liuqi.common.base.service.AbstractBaseService;
import com.liuqi.common.exception.AppException;
import com.liuqi.common.utils.DynamicSqlHelper;
import com.liuqi.dua.bean.dto.DsDTO;
import com.liuqi.dua.bean.query.DsQuery;
import com.liuqi.dua.domain.entity.DsEntity;
import com.liuqi.dua.domain.mapper.DsMapper;
import com.liuqi.dua.executor.DynamicDsConfigService;
import com.liuqi.dua.service.DsService;
import jakarta.annotation.PostConstruct;
import liquibase.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据源配置服务实现
 *
 * @author Coder Generator 2024-08-09 11:43:39
 **/
@Service
@Slf4j
public class DsServiceImpl extends AbstractBaseService<DsEntity, DsDTO, DsMapper, DsQuery> implements DsService {
    @Autowired
    private DynamicDsConfigService dynamicDsConfigService;

    @PostConstruct
    public void init() {
        // 加载所有数据源
        this.findAll().forEach(dynamicDsConfigService::loadDs);

        log.info("动态数据源加载完毕");
    }

    @Override
    public DsDTO toDTO(DsEntity entity) {
        DsDTO dto = new DsDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    @Override
    public DsEntity toEntity(DsDTO dto) {
        DsEntity entity = new DsEntity();
        BeanUtils.copyProperties(dto, entity);
        return entity;
    }

    @Override
    protected QueryWrapper<DsEntity> queryToWrapper(DsQuery query) {
        return this.createQueryWrapper()
                .eq(StringUtils.isNotBlank(query.getId()), "id", query.getId())
                .in(null != query.getIds(), "id", query.getIds())
                .eq("deleted", false)
                .eq(StringUtils.isNotBlank(query.getCode()), "code", query.getCode())
                .orderByDesc("create_time");
    }

    /**
     * 实体插入操作
     *
     * @param dto 插入的数据对象
     * @return 插入完成后的对象，包含有生成的id
     */
    @Override
    public DsDTO insert(DsDTO dto) {
        dto = super.insert(dto);

        dynamicDsConfigService.loadDs(dto);

        return dto;
    }

    /**
     * 更新记录
     *
     * @param dto 待更新记录内容，id不能为空
     */
    @Override
    public void update(DsDTO dto) {
        super.update(dto);

        dynamicDsConfigService.loadDs(dto);
    }

    /**
     * 获取数据源表清单
     *
     * @param ds 数据源编码
     * @return 数据源表清单
     */
    @Override
    public List<String> getTables(String ds) {
        String schema = getSchema(ds);

        DynamicDataSourceContextHolder.push(ds);
        try {
            //noinspection unchecked
            List<Map<String, Object>> list = (List<Map<String, Object>>) DynamicSqlHelper.executeSql("dsBase",
                    "select table_name from information_schema.tables where table_schema='" + schema + "'",
                    new HashMap<>(1));
            return list.stream().map(map -> MapUtils.getString(map, "tableName"))
                    .toList();
        } finally {
            DynamicDataSourceContextHolder.poll();
        }
    }

    /**
     * 获取DS对应的库名
     *
     * @param ds 数据源编码
     * @return 对应的数据库名称
     */
    private String getSchema(String ds) {
        DsDTO dsDTO = this.findByCode(ds);
        String url = dsDTO.getUrl();

        String schema = url.replace("jdbc:mysql://", "");
        int idx = schema.indexOf("/");
        schema = schema.substring(idx + 1);
        idx = schema.indexOf("?");
        if (idx >= 0) {
            schema = schema.substring(0, idx);
        }
        return schema;
    }

    private DsDTO findByCode(String ds) {
        DsQuery query = new DsQuery();
        query.setCode(ds);
        return this.findOne(query).orElse(null);
    }

    /**
     * 查找表字段列表
     *
     * @param ds    数据源
     * @param table 表名
     * @return 表对应的字段列表
     */
    @Override
    public List<String> getTableFields(String ds, String table) {
        String schema = getSchema(ds);

        DynamicDataSourceContextHolder.push(ds);
        try {
            String sql = "select column_name from information_schema.columns where TABLE_SCHEMA = '" + schema + "' and TABLE_NAME = '" + table + "'";
            //noinspection unchecked
            List<Map<String, Object>> list = (List<Map<String, Object>>) DynamicSqlHelper.executeSql("dsBase-columns",
                    sql,
                    new HashMap<>(1));
            return list.stream().map(item -> MapUtils.getString(item, "columnName"))
                    .toList();
        } finally {
            DynamicDataSourceContextHolder.poll();
        }
    }

    @Override
    public List<Map<String, Object>> getTableFieldsFull(String ds, String table) {
        String schema = getSchema(ds);

        DynamicDataSourceContextHolder.push(ds);
        try {
            String sql = "select * from information_schema.columns where TABLE_SCHEMA = '" + schema + "' and TABLE_NAME = '" + table + "'";
            //noinspection unchecked
            return (List<Map<String, Object>>) DynamicSqlHelper.executeSql("dsBase-columns",
                    sql,
                    new HashMap<>(1));
        } finally {
            DynamicDataSourceContextHolder.poll();
        }
    }
}
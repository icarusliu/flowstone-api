package com.liuqi.dua.service.db;

import com.baomidou.dynamic.datasource.toolkit.DynamicDataSourceContextHolder;
import com.liuqi.common.ErrorCodes;
import com.liuqi.common.exception.AppException;
import com.liuqi.dua.bean.dto.DsDTO;
import com.liuqi.dua.bean.dto.TableFieldDTO;
import com.liuqi.dua.service.DbMetadataService;
import com.liuqi.dua.service.DsService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;

/**
 * 数据库元数据处理辅助
 *
 * @author LiuQi 2024/9/26-16:02
 * @version V1.0
 **/
@Service
public class DbMetadataHelper implements InitializingBean {
    @Autowired
    private Set<DbMetadataService> metadataServices;

    private final Map<String, DbMetadataService> metadataServiceMap = new HashMap<>(16);

    @Autowired
    @Lazy
    private DsService dsService;

    @Override
    public void afterPropertiesSet() {
        metadataServices.forEach(item -> metadataServiceMap.put(item.getKey(), item));
    }

    /**
     * 从连接串中解析库名
     *
     * @param url 连接池
     * @return 数据库
     */
    private String parseSchema(String type, String url) {
        return getMetadataService(type).parseSchema(url);
    }

    /**
     * 根据编码取得对应数据库服务
     *
     * @param type 编码，mysql或者其它
     * @return 对应的数据库元服务
     */
    private DbMetadataService getMetadataService(String type) {
        DbMetadataService metadataService = metadataServiceMap.get(type);
        if (null == metadataService) {
            throw AppException.of(ErrorCodes.BASE_DS_INVALID_DB);
        }

        return metadataService;
    }

    /**
     * 数据库操作
     *
     * @param ds       数据源编码
     * @param function 执行函数
     * @param <R>      执行结果对象类型
     * @return 函数执行结果
     */
    private <R> R process(String ds, BiFunction<String, String, R> function) {
        DsDTO dsDTO = dsService.findByCode(ds);
        String schema = parseSchema(dsDTO.getType(), dsDTO.getUrl());

        DynamicDataSourceContextHolder.push(ds);
        try {
            return function.apply(dsDTO.getType(), schema);
        } finally {
            DynamicDataSourceContextHolder.poll();
        }
    }

    /**
     * 获取数据库表清单
     *
     * @param ds 数据源
     * @return 数据源对应的库的表清单
     */
    public List<String> getTables(String ds) {
        return this.process(ds, (type, schema) -> getMetadataService(type).getTables(schema));
    }

    /**
     * 获取数据库表字段清单
     *
     * @param ds    数据源编码
     * @param table 表名
     * @return 表对应的字段清单
     */
    public List<TableFieldDTO> getTableFields(String ds, String table) {
        return this.process(ds, (type, schema) -> getMetadataService(type).getTableFields(schema, table));
    }

    /**
     * 获取数据源driver class 名称
     */
    public String getDriverClass(String type) {
        return this.getMetadataService(type).getDriverClass();
    }


    public void test(DsDTO dsDTO) {
        String schema = parseSchema(dsDTO.getType(), dsDTO.getUrl());
        DynamicDataSourceContextHolder.push(dsDTO.getCode());
        try {
             this.getMetadataService(dsDTO.getType()).test(schema);
        } finally {
            DynamicDataSourceContextHolder.poll();
        }
    }
}

package com.liuqi.dua.service.db;

import com.liuqi.common.utils.DynamicSqlHelper;
import com.liuqi.dua.bean.dto.TableFieldDTO;
import com.liuqi.dua.service.DbMetadataService;
import org.apache.commons.collections4.MapUtils;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * MySql元数据服务
 *
 * @author LiuQi 2024/9/26-15:59
 * @version V1.0
 **/
@Service
public class MysqlMetadataService implements DbMetadataService {
    @Override
    public String getKey() {
        return "mysql";
    }

    @Override
    public String getDriverClass() {
        return "com.mysql.cj.jdbc.Driver";
    }

    /**
     * 从连接串中解析库名
     *
     * @param url 连接池
     * @return 数据库
     */
    @Override
    public String parseSchema(String url) {
        String schema = url.replace("jdbc:mysql://", "");
        int idx = schema.indexOf("/");
        schema = schema.substring(idx + 1);
        idx = schema.indexOf("?");
        if (idx >= 0) {
            schema = schema.substring(0, idx);
        }
        return schema;
    }

    /**
     * 获取数据库表清单
     *
     * @param schema 数据库
     * @return 数据源对应的库的表清单
     */
    @Override
    public List<String> getTables(String schema) {
        //noinspection unchecked
        List<Map<String, Object>> list = (List<Map<String, Object>>) DynamicSqlHelper.executeSql("dsBase",
                "select table_name from information_schema.tables where table_schema='" + schema + "'",
                new HashMap<>(1));
        return list.stream().map(map -> MapUtils.getString(map, "tableName"))
                .toList();
    }

    /**
     * 获取数据库表字段清单
     *
     * @param schema 数据库
     * @param table  表名
     * @return 表对应的字段清单
     */
    @Override
    public List<TableFieldDTO> getTableFields(String schema, String table) {
        String sql = "select * from information_schema.columns where TABLE_SCHEMA = '" + schema + "' and TABLE_NAME = '" + table + "'";
        //noinspection unchecked
        List<Map<String, Object>> fields = (List<Map<String, Object>>) DynamicSqlHelper.executeSql("dsBase-columns",
                sql,
                new HashMap<>(1));

        return fields.stream().map(map -> {
            TableFieldDTO dto = new TableFieldDTO();
            dto.setField(MapUtils.getString(map, "columnName"));
            dto.setType(MapUtils.getString(map, "columnType"));
            return dto;
        }).toList();
    }

    @Override
    public void test(String schema) {
        String sql = "select * 1";
        DynamicSqlHelper.executeSql("test", sql, new HashMap<>());
    }
}

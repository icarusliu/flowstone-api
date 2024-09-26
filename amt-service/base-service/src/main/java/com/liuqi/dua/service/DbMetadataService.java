package com.liuqi.dua.service;

import com.liuqi.dua.bean.dto.TableFieldDTO;

import java.util.List;

/**
 * 数据库基础操作服务
 *
 * @author LiuQi 2024/9/26-15:54
 * @version V1.0
 **/
public interface DbMetadataService {
    /**
     * 获取数据库类型编码
     *
     * @return 数据库类型编码
     */
    String getKey();

    /**
     * 获取数据库对应的jdbc driver名称
     *
     * @return driver类名
     */
    String getDriverClass();

    /**
     * 从连接串中解析库名
     *
     * @param url 连接池
     * @return 数据库
     */
    String parseSchema(String url);

    /**
     * 获取数据库表清单
     *
     * @param ds 数据源
     * @return 数据源对应的库的表清单
     */
    List<String> getTables(String ds);

    /**
     * 获取数据库表字段清单
     *
     * @param ds    数据源编码
     * @param table 表名
     * @return 表对应的字段清单
     */
    List<TableFieldDTO> getTableFields(String ds, String table);

    /**
     * 测试SQL
     *
     * @param schema 数据库
     */
    void test(String schema);
}

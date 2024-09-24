package com.liuqi.dua.service;

import com.liuqi.common.base.service.BaseService;
import com.liuqi.dua.bean.dto.DsDTO;
import com.liuqi.dua.bean.query.DsQuery;

import java.util.List;
import java.util.Map;

/**
 * 数据源配置服务接口
 *
 * @author Coder Generator 2024-08-09 11:43:39
 **/
public interface DsService extends BaseService<DsDTO, DsQuery> {
    /**
     * 获取数据源表清单
     *
     * @param ds 数据源编码
     * @return 数据源表清单
     */
    List<String> getTables(String ds);

    /**
     * 查找表字段列表
     *
     * @param ds    数据源
     * @param table 表名
     * @return 表对应的字段列表
     */
    List<String> getTableFields(String ds, String table);

    /**
     * 查询表字段列表
     *
     * @param ds    数据源
     * @param table 表名
     * @return 表对应的字段列表
     */
    List<Map<String, Object>> getTableFieldsFull(String ds, String table);
}
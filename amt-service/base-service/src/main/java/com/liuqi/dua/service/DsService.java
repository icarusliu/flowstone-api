package com.liuqi.dua.service;

import com.liuqi.common.base.service.BaseService;
import com.liuqi.dua.bean.dto.DsDTO;
import com.liuqi.dua.bean.dto.TableFieldDTO;
import com.liuqi.dua.bean.query.DsQuery;

import java.util.List;

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

    DsDTO findByCode(String ds);

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
    List<TableFieldDTO> getTableFieldsFull(String ds, String table);

    /**
     * 测试连接
     *
     * @param dsDTO 数据源信息
     */
    void testConnect(DsDTO dsDTO);
}
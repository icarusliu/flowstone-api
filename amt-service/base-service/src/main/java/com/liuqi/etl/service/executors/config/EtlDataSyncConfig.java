package com.liuqi.etl.service.executors.config;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 数据抽取配置
 *
 * @author  LiuQi 2025/3/10-18:05
 * @version V1.0
 **/
@Data
public class EtlDataSyncConfig {
    private Boolean advanced;

    /**
     * 源表数据源
     */
    private String sourceDs;

    /**
     * 来源表
     */
    private String sourceTable;

    /**
     * 来源条件
     */
    private String sourceWhere;

    /**
     * 源表查询SQL
     */
    private String sourceSql;

    /**
     * 目标数据源
     */
    private String destDs;

    /**
     * 目标表
     */
    private String destTable;

    /**
     * 存储规则
     */
    private String storeRule;

    /**
     * 常规模式下的字段映射
     */
    private List<Map<String, Object>> fieldReflect;

    /**
     * 目标SQL
     */
    private String destSql;

    /**
     * 目标SQL执行前语句
     */
    private String prepareSql;

    /**
     * 批次大小
     */
    private Integer batchSize = 1000;
}

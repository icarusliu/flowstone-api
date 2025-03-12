package com.liuqi.etl.bean.dto;

import lombok.Data;

/**
 * 数据抽取配置
 *
 * @author  LiuQi 2025/3/10-18:05
 * @version V1.0
 **/
@Data
public class EtlDataSyncConfig {
    /**
     * 源表数据源
     */
    private String sourceDs;

    /**
     * 源表查询SQL
     */
    private String sourceSql;

    /**
     * 目标数据源
     */
    private String destDs;

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

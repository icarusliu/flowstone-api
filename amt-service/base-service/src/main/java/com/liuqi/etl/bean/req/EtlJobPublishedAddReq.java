package com.liuqi.etl.bean.req;

import lombok.Data;

import java.util.Map;

/**
 * ETL已发布任务新增对象 
 * @author Coder Generator 2025-03-11 21:48:25 
 **/
@Data
public class EtlJobPublishedAddReq {
    /**
     * 任务类型，sync:数据同步任务；process：数据处理任务
     */
    private String type;
    /**
     * 任务编码
     */
    private String code;
    /**
     * 任务名称
     */
    private String name;
    /**
     * 是否自动触发，即当期依赖的任务执行完成后是否自动触发
     */
    private Boolean autoTrigger;
    /**
     * 任务运行配置
     */
    private Map<String, Object> config;
    /**
     * 任务其它配置
     */
    private Map<String, Object> metadata;
    /**
     * 定时执行的表达式
     */
    private String cron;
    /**
     * 已发布版本
     */
    private Integer publishedVersion;
}
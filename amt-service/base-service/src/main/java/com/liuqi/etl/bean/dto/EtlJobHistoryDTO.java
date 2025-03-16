package com.liuqi.etl.bean.dto;

import com.liuqi.common.base.bean.dto.BaseDTO;
import lombok.Data;

import java.util.Map;

/**
 * ETL任务历史记录数据实体 
 * @author Coder Generator 2025-03-16 17:21:59 
 **/
@Data
public class EtlJobHistoryDTO extends BaseDTO {
    /**
     * 任务id
     */
    private String jobId;
    /**
     * 分类id
     */
    private String typeId;
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
     * 是否启用
     */
    private Boolean enabled;
    /**
     * 排序序号
     */
    private String sort;
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
     * 任务版本
     */
    private Integer version;
    /**
     * 已发布版本
     */
    private Integer publishedVersion;
}
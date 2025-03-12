package com.liuqi.etl.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.FastjsonTypeHandler;
import com.liuqi.common.base.domain.entity.BaseEntity;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * ETL已发布任务
 *
 * @author  LiuQi 2024/8/5-15:37
 * @version V1.0
 **/
@Data
@TableName(value = "b_etl_job_published", autoResultMap = true)
public class EtlJobPublishedEntity extends BaseEntity {
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
    @TableField(typeHandler = FastjsonTypeHandler.class)
    private Map<String, Object> config;

    /**
     * 任务其它配置
     */
    @TableField(typeHandler = FastjsonTypeHandler.class)
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

package com.liuqi.etl.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.liuqi.common.base.domain.entity.BaseEntity;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * ETL执行日志
 *
 * @author  LiuQi 2024/10/31-20:19
 * @version V1.0
 **/
@Data
@TableName("b_etl_log")
public class EtlLogEntity extends BaseEntity {
    /**
     * 执行的任务id
     */
    private String jobId;

    private String jobCode;

    private String jobName;

    /**
     * 执行时间
     */
    private LocalDateTime executeTime;

    /**
     * 执行参数
     */
    private String params;

    /**
     * 执行状态，0：成功；1：失败；
     */
    private Integer status;

    /**
     * 执行结果
     */
    private String result;

    /**
     * 异常消息
     */
    private String errorMsg;

    /**
     * 消耗时间
     */
    private Integer spentTime;

    /**
     * 执行的数据日期
     */
    private LocalDate dataDate;
}

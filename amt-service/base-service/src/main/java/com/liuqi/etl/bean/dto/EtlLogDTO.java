package com.liuqi.etl.bean.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.liuqi.common.base.bean.dto.BaseDTO;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * ETL执行日志数据实体 
 * @author Coder Generator 2025-03-10 16:37:56 
 **/
@Data
public class EtlLogDTO extends BaseDTO {
    /**
     * 执行的任务id
     */
    private String jobId;

    private String jobCode;

    private String jobName;

    /**
     * 执行时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
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
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dataDate;

    public static EtlLogDTO fromJob(EtlJobPublishedDTO job) {
        EtlLogDTO dto = new EtlLogDTO();
        dto.setExecuteTime(LocalDateTime.now());
        dto.setJobId(job.getId());
        dto.setJobName(job.getName());
        dto.setJobCode(job.getCode());
        return dto;
    }
}
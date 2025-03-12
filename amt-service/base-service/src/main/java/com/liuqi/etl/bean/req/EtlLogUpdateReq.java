package com.liuqi.etl.bean.req;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * ETL执行日志更新对象 
 * @author Coder Generator 2025-03-10 16:37:56 
 **/
@Data
public class EtlLogUpdateReq {
    private String id;
    /**
     * 执行的任务id
     */
    private String jobId;
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
}
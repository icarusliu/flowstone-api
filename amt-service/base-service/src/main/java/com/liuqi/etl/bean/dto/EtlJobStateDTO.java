package com.liuqi.etl.bean.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.liuqi.common.base.bean.dto.BaseDTO;
import lombok.Data;

import java.time.LocalDate;

/**
 * 任务状态
 * @author Coder Generator 2025-03-10 17:53:08 
 **/
@Data
public class EtlJobStateDTO extends BaseDTO {
    /**
     * 任务id
     */
    private String jobId;
    /**
     * 执行日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate executeDate;

    /**
     * 执行状态 0未执行，1正在执行，2执行完成，执行异常
     */
    private Integer state;

    /**
     * 是否自动执行子任务
     */
    private Boolean runSub;
}
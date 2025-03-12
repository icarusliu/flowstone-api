package com.liuqi.etl.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.liuqi.common.base.domain.entity.BaseEntity;
import lombok.Data;

import java.time.LocalDate;

/**
 * 任务执行状态
 * 后续可以考虑使用Redis替换记录功能
 * 主要用于触发多父任务的子任务的执行
 *
 * @author  LiuQi 2025/3/10-17:49
 * @version V1.0
 **/
@Data
@TableName("b_etl_job_state")
public class EtlJobStateEntity extends BaseEntity {
    /**
     * 任务id
     */
    private String jobId;

    /**
     * 执行日期
     */
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

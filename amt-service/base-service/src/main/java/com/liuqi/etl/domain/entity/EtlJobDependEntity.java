package com.liuqi.etl.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.liuqi.common.base.domain.entity.BaseEntity;
import lombok.Data;

/**
 * 任务依赖
 *
 * @author  LiuQi 2025/3/10-16:43
 * @version V1.0
 **/
@Data
@TableName("b_etl_job_depend")
public class EtlJobDependEntity extends BaseEntity {
    /**
     * 当前任务id
     */
    private String jobId;

    /**
     * 父级任务id
     */
    private String parentJobId;
}

package com.liuqi.etl.bean.dto;

import com.liuqi.common.base.bean.dto.BaseDTO;
import lombok.Data;

/**
 * 任务依赖数据实体 
 * @author Coder Generator 2025-03-10 17:05:12 
 **/
@Data
public class EtlJobDependDTO extends BaseDTO {
    /**
     * 当前任务id
     */
    private String jobId;
    /**
     * 父级任务id
     */
    private String parentJobId;
}
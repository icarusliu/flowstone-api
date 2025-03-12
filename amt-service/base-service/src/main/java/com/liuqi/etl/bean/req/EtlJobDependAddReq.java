package com.liuqi.etl.bean.req;

import lombok.Data;

/**
 * 任务依赖新增对象 
 * @author Coder Generator 2025-03-10 17:05:12 
 **/
@Data
public class EtlJobDependAddReq {
    /**
     * 当前任务id
     */
    private String jobId;
    /**
     * 父级任务id
     */
    private String parentJobId;
}
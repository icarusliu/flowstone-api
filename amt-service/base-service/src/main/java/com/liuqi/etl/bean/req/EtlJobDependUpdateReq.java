package com.liuqi.etl.bean.req;

import lombok.Data;

import java.util.List;

/**
 * 任务依赖更新对象 
 * @author Coder Generator 2025-03-10 17:05:12 
 **/
@Data
public class EtlJobDependUpdateReq {
    /**
     * 当前任务id
     */
    private String jobId;
    /**
     * 父级任务id
     */
    private List<String> dependJobIds;
}
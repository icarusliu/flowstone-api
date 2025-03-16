package com.liuqi.etl.bean.resp;

import lombok.Data;

import java.util.List;

/**
 * 血缘树
 *
 * @author  LiuQi 2025/3/16-20:26
 * @version V1.0
 **/
@Data
public class BloodTree {
    /**
     * 表
     */
    private String table;

    /**
     * 涉及任务id
     */
    private String jobId;

    /**
     * 涉及任务编号
     */
    private String jobCode;

    /**
     * 涉及任务名称
     */
    private String jobName;

    /**
     * 后代列表
     */
    private List<BloodTree> children;
}

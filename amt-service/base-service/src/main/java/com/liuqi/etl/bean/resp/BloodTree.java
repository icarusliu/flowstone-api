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
    private List<String> usedTables;

    /**
     * 更新的表
     */
    private List<String> updatedTables;

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

    /**
     * 父id列表
     */
    private List<String> parentIds;

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof BloodTree)) {
            return false;
        }

        return ((BloodTree) obj).getJobId().equals(jobId);
    }
}

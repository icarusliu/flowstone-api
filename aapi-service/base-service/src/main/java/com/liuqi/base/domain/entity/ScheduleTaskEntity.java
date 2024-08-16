package com.liuqi.base.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.liuqi.common.annotations.Comment;
import com.liuqi.common.base.domain.entity.BaseEntity;
import lombok.Data;

import java.util.Map;

/**
 * 定时任务
 *
 * @author  LiuQi 2024/8/16-15:51
 * @version V1.0
 **/
@Data
@Comment("定时任务")
@TableName("base_schedule_task")
public class ScheduleTaskEntity extends BaseEntity {
    private String name;
    private String cron;
    private String apiId;
    private String remark;
    private Map<String, Object> params;
    private Integer status;
    private Boolean started;
}

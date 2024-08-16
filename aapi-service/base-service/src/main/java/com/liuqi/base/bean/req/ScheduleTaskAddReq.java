package com.liuqi.base.bean.req;

import lombok.Data;

import java.util.Map;

/**
 * 定时任务新增对象
 *
 * @author Coder Generator 2024-08-16 15:54:28
 **/
@Data
public class ScheduleTaskAddReq {
    private String name;
    private String cron;
    private String apiId;
    private String remark;
    private Map<String, Object> params;
    private Integer status;
}
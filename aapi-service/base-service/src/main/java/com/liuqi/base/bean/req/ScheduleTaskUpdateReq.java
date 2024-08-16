package com.liuqi.base.bean.req;

import lombok.Data;

import java.util.Map;

/**
 * 定时任务更新对象
 *
 * @author Coder Generator 2024-08-16 15:54:28
 **/
@Data
public class ScheduleTaskUpdateReq {
    private String id;
    private String name;
    private String cron;
    private String apiId;
    private String remark;
    private Map<String, Object> params;
    private Integer status;
}
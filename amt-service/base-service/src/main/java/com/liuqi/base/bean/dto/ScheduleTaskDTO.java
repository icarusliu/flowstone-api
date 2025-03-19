package com.liuqi.base.bean.dto;

import com.liuqi.common.base.bean.dto.BaseDTO;
import lombok.Data;

import java.util.Map;

/**
 * 定时任务数据实体
 *
 * @author Coder Generator 2024-08-16 15:54:28
 **/
@Data
public class ScheduleTaskDTO extends BaseDTO {
    private String name;
    private String cron;
    private String apiId;
    private String apiName;
    private String remark;
    private Map<String, Object> params;
    private Integer status;
    private Boolean started;
}
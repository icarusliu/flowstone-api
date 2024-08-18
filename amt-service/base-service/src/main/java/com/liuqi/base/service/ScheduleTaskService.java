package com.liuqi.base.service;

import com.liuqi.base.bean.dto.ScheduleTaskDTO;
import com.liuqi.base.bean.query.ScheduleTaskQuery;
import com.liuqi.common.base.service.BaseService;

/**
 * 定时任务服务接口
 *
 * @author Coder Generator 2024-08-16 15:54:28
 **/
public interface ScheduleTaskService extends BaseService<ScheduleTaskDTO, ScheduleTaskQuery> {
    /**
     * 更新任务状态到指定状态
     *
     * @param id      任务id
     * @param started 启动状态
     * @return 更新记录数量
     */
    boolean updateStarted(String id, Boolean started);


}
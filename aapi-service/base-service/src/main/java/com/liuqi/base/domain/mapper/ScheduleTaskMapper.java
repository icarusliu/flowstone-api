package com.liuqi.base.domain.mapper; /**
 * 定时任务数据库操作服务
 *
 * @author Coder Generator 2024-08-16 15:54:28
 **/

import com.liuqi.base.domain.entity.ScheduleTaskEntity;
import com.liuqi.common.base.domain.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ScheduleTaskMapper extends BaseMapper<ScheduleTaskEntity> {
}
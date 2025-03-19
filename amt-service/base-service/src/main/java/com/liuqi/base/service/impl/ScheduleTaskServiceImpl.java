package com.liuqi.base.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.liuqi.base.bean.dto.ScheduleTaskDTO;
import com.liuqi.base.bean.query.ScheduleTaskQuery;
import com.liuqi.base.domain.entity.ScheduleTaskEntity;
import com.liuqi.base.domain.mapper.ScheduleTaskMapper;
import com.liuqi.base.service.ScheduleTaskService;
import com.liuqi.common.base.service.AbstractBaseService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

/**
 * 定时任务服务实现
 *
 * @author Coder Generator 2024-08-16 15:54:28
 **/
@Service
public class ScheduleTaskServiceImpl extends AbstractBaseService<ScheduleTaskEntity, ScheduleTaskDTO, ScheduleTaskMapper, ScheduleTaskQuery> implements ScheduleTaskService {
    @Override
    public ScheduleTaskDTO toDTO(ScheduleTaskEntity entity) {
        ScheduleTaskDTO dto = new ScheduleTaskDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    @Override
    public ScheduleTaskEntity toEntity(ScheduleTaskDTO dto) {
        ScheduleTaskEntity entity = new ScheduleTaskEntity();
        BeanUtils.copyProperties(dto, entity);
        return entity;
    }

    @Override
    protected QueryWrapper<ScheduleTaskEntity> queryToWrapper(ScheduleTaskQuery query) {
        return this.createQueryWrapper()
                .eq(StringUtils.isNotBlank(query.getId()), "id", query.getId())
                .in(null != query.getIds(), "id", query.getIds())
                .eq("deleted", false)
                .orderByDesc("create_time");
    }

    /**
     * 更新任务状态到指定状态
     *
     * @param id      任务id
     * @param started 启动状态
     * @return 更新记录数量
     */
    @Override
    public boolean updateStarted(String id, Boolean started) {
        UpdateWrapper<ScheduleTaskEntity> updateWrapper = Wrappers.<ScheduleTaskEntity>update()
                .set("started", started)
                .eq("id", id)
                .eq("started", !started);
        return this.update(updateWrapper);
    }
}
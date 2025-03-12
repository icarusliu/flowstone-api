package com.liuqi.etl.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.liuqi.common.base.service.AbstractBaseService;
import com.liuqi.etl.bean.dto.EtlJobStateDTO;
import com.liuqi.etl.bean.query.EtlJobStateQuery;
import com.liuqi.etl.domain.entity.EtlJobStateEntity;
import com.liuqi.etl.domain.mapper.EtlJobStateMapper;
import com.liuqi.etl.service.EtlJobStateService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * 执行完成任务记录服务实现 
 * @author Coder Generator 2025-03-10 17:53:08 
 **/
@Service
public class EtlJobStateServiceImpl extends AbstractBaseService<EtlJobStateEntity, EtlJobStateDTO, EtlJobStateMapper, EtlJobStateQuery> implements EtlJobStateService {
    @Override
    public EtlJobStateDTO toDTO(EtlJobStateEntity entity) {
        EtlJobStateDTO dto = new EtlJobStateDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    @Override
    public EtlJobStateEntity toEntity(EtlJobStateDTO dto) {
        EtlJobStateEntity entity = new EtlJobStateEntity();
        BeanUtils.copyProperties(dto, entity);
        return entity;
    }

    @Override
    protected QueryWrapper<EtlJobStateEntity> queryToWrapper(EtlJobStateQuery query) {
        return this.createQueryWrapper()
                .eq(StringUtils.isNotBlank(query.getId()), "id", query.getId())
                .in(null != query.getIds(), "id", query.getIds())
                .in(null != query.getJobIds(), "job_id", query.getJobIds())
                .eq(null != query.getExecuteDate(), "execute_date", query.getExecuteDate())
                .eq(null != query.getState(), "state", query.getState())
                .in(null != query.getStates(), "state", query.getStates())
                .orderByDesc("create_time");
    }

    /**
     * 实体插入操作
     *
     * @param dto 插入的数据对象
     * @return 插入完成后的对象，包含有生成的id
     */
    @Override
    public EtlJobStateDTO insert(EtlJobStateDTO dto) {
        String id = UUID.randomUUID().toString().replaceAll("-", "");
        dto.setId(id);
        baseMapper.upsert(dto);
        return dto;
    }

    /**
     * 更新记录
     *
     * @param dto 待更新记录内容，id不能为空
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Async
    public void update(EtlJobStateDTO dto) {
        super.update(dto);
    }
}
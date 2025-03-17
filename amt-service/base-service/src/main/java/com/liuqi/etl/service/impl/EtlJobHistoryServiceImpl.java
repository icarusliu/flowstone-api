package com.liuqi.etl.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.liuqi.common.base.service.AbstractBaseService;
import com.liuqi.etl.bean.dto.EtlJobHistoryDTO;
import com.liuqi.etl.bean.query.EtlJobHistoryQuery;
import com.liuqi.etl.domain.entity.EtlJobHistoryEntity;
import com.liuqi.etl.domain.mapper.EtlJobHistoryMapper;
import com.liuqi.etl.service.EtlJobHistoryService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

/**
 * ETL任务历史记录服务实现 
 * @author Coder Generator 2025-03-16 17:21:59 
 **/
@Service
public class EtlJobHistoryServiceImpl extends AbstractBaseService<EtlJobHistoryEntity, EtlJobHistoryDTO, EtlJobHistoryMapper, EtlJobHistoryQuery> implements EtlJobHistoryService {
    @Override
    public EtlJobHistoryDTO toDTO(EtlJobHistoryEntity entity) {
        EtlJobHistoryDTO dto = new EtlJobHistoryDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    @Override
    public EtlJobHistoryEntity toEntity(EtlJobHistoryDTO dto) {
        EtlJobHistoryEntity entity = new EtlJobHistoryEntity();
        BeanUtils.copyProperties(dto, entity);
        return entity;
    }

    @Override
    protected QueryWrapper<EtlJobHistoryEntity> queryToWrapper(EtlJobHistoryQuery query) {
        return this.createQueryWrapper()
                .eq(StringUtils.isNotBlank(query.getId()), "id", query.getId())
                .in(null != query.getIds(), "id", query.getIds())
                .eq(StringUtils.isNotBlank(query.getJobId()), "job_id", query.getJobId())
                .orderByDesc("create_time");
    }
}
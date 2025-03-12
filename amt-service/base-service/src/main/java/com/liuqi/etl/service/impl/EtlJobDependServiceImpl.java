package com.liuqi.etl.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.liuqi.common.base.service.AbstractBaseService;
import com.liuqi.etl.bean.dto.EtlJobDependDTO;
import com.liuqi.etl.bean.query.EtlJobDependQuery;
import com.liuqi.etl.domain.entity.EtlJobDependEntity;
import com.liuqi.etl.domain.mapper.EtlJobDependMapper;
import com.liuqi.etl.service.EtlJobDependService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * 任务依赖服务实现 
 * @author Coder Generator 2025-03-10 17:05:53 
 **/
@Service
public class EtlJobDependServiceImpl extends AbstractBaseService<EtlJobDependEntity, EtlJobDependDTO, EtlJobDependMapper, EtlJobDependQuery> implements EtlJobDependService {
    @Override
    public EtlJobDependDTO toDTO(EtlJobDependEntity entity) {
        EtlJobDependDTO dto = new EtlJobDependDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    @Override
    public EtlJobDependEntity toEntity(EtlJobDependDTO dto) {
        EtlJobDependEntity entity = new EtlJobDependEntity();
        BeanUtils.copyProperties(dto, entity);
        return entity;
    }

    @Override
    protected QueryWrapper<EtlJobDependEntity> queryToWrapper(EtlJobDependQuery query) {
        return this.createQueryWrapper()
                .eq(StringUtils.isNotBlank(query.getId()), "id", query.getId())
                .in(null != query.getIds(), "id", query.getIds())
                .eq(StringUtils.isNotBlank(query.getParentJobId()), "parent_job_id", query.getParentJobId())
                .in(null != query.getJobIds(), "job_id", query.getJobIds())
                .eq(StringUtils.isNotBlank(query.getJobId()), "job_id", query.getJobId())
                .orderByDesc("create_time");
    }

    /**
     * 更新任务依赖
     */
    @Override
    public void update(String jobId, List<String> dependJobIds) {
        if (StringUtils.isBlank(jobId)) {
            return;
        }

        this.remove(this.createQueryWrapper().eq("job_id", jobId));

        if (CollectionUtils.isEmpty(dependJobIds)) {
            return;
        }

        List<EtlJobDependDTO> list = dependJobIds.stream()
                .map(id -> {
                    EtlJobDependDTO item = new EtlJobDependDTO();
                    item.setJobId(jobId);
                    item.setParentJobId(id);
                    return item;
                }).toList();
        this.insert(list);
    }
}
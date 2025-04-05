package com.liuqi.etl.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.liuqi.common.base.service.AbstractBaseEntityService;
import com.liuqi.etl.bean.dto.EtlLogDTO;
import com.liuqi.etl.bean.query.EtlLogQuery;
import com.liuqi.etl.domain.entity.EtlLogEntity;
import com.liuqi.etl.domain.mapper.EtlLogMapper;
import com.liuqi.etl.service.EtlLogService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

/**
 * ETL执行日志服务实现 
 * @author Coder Generator 2025-03-10 16:37:56 
 **/
@Service
public class EtlLogServiceImpl extends AbstractBaseEntityService<EtlLogEntity, EtlLogDTO, EtlLogMapper, EtlLogQuery> implements EtlLogService {
    @Override
    public EtlLogDTO toDTO(EtlLogEntity entity) {
        EtlLogDTO dto = new EtlLogDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    @Override
    public EtlLogEntity toEntity(EtlLogDTO dto) {
        EtlLogEntity entity = new EtlLogEntity();
        BeanUtils.copyProperties(dto, entity);
        return entity;
    }

    @Override
    protected QueryWrapper<EtlLogEntity> queryToWrapper(EtlLogQuery query) {
        return this.createQueryWrapper()
                .eq(StringUtils.isNotBlank(query.getId()), "id", query.getId())
                .in(null != query.getIds(), "id", query.getIds())
                .ge(null != query.getBeginDate(), "execute_time", query.getBeginDate())
                .le(null != query.getEndDate(), "execute_time", query.getEndDate())
                .and(StringUtils.isNotBlank(query.getKey()), q -> {
                    q.like("job_code", query.getKey())
                            .or()
                            .like("job_name", query.getKey());
                })
                .eq(null != query.getStatus(), "status", query.getStatus())
                .orderByDesc("create_time");
    }

    /**
     * 实体插入操作
     *
     * @param dto 插入的数据对象
     * @return 插入完成后的对象，包含有生成的id
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public EtlLogDTO insert(EtlLogDTO dto) {
        return super.insert(dto);
    }

    /**
     * 日志清理
     *
     * @param localDate 清理指定日期前的日志
     */
    @Override
    public void clearLogsBefore(LocalDate localDate) {
        QueryWrapper<EtlLogEntity> queryWrapper = this.createQueryWrapper();
        queryWrapper.le("create_time", localDate);
        this.remove(queryWrapper);
    }
}
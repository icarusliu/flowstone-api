package com.liuqi.etl.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.liuqi.common.base.service.AbstractBaseEntityService;
import com.liuqi.etl.bean.dto.EtlJobPublishedDTO;
import com.liuqi.etl.bean.query.EtlJobPublishedQuery;
import com.liuqi.etl.domain.entity.EtlJobPublishedEntity;
import com.liuqi.etl.domain.mapper.EtlJobPublishedMapper;
import com.liuqi.etl.service.EtlJobPublishedService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

/**
 * ETL已发布任务服务实现 
 * @author Coder Generator 2025-03-11 21:48:25 
 **/
@Service
public class EtlJobPublishedServiceImpl extends AbstractBaseEntityService<EtlJobPublishedEntity, EtlJobPublishedDTO, EtlJobPublishedMapper, EtlJobPublishedQuery> implements EtlJobPublishedService {
    @Override
    public EtlJobPublishedDTO toDTO(EtlJobPublishedEntity entity) {
        EtlJobPublishedDTO dto = new EtlJobPublishedDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    @Override
    public EtlJobPublishedEntity toEntity(EtlJobPublishedDTO dto) {
        EtlJobPublishedEntity entity = new EtlJobPublishedEntity();
        BeanUtils.copyProperties(dto, entity);
        return entity;
    }

    @Override
    protected QueryWrapper<EtlJobPublishedEntity> queryToWrapper(EtlJobPublishedQuery query) {
        return this.createQueryWrapper()
                .eq(StringUtils.isNotBlank(query.getId()), "id", query.getId())
                .in(null != query.getIds(), "id", query.getIds())
                .eq(null != query.getAutoTrigger(), "auto_trigger", query.getAutoTrigger())
                .isNotNull(null != query.getCronNotNull() && query.getCronNotNull(), "cron")
                .eq(null != query.getType(), "type", query.getType())
                .orderByDesc("create_time");
    }


}
package com.liuqi.etl.domain.mapper;

import com.liuqi.common.base.domain.mapper.BaseMapper;
import com.liuqi.etl.bean.dto.EtlJobDTO;
import com.liuqi.etl.bean.query.EtlJobQuery;
import com.liuqi.etl.domain.entity.EtlJobEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * ETL任务数据库操作服务 
 * @author Coder Generator 2025-03-10 16:36:10 
 **/
@Mapper
public interface EtlJobMapper extends BaseMapper<EtlJobEntity> {
    List<EtlJobDTO> query(EtlJobQuery query);
}
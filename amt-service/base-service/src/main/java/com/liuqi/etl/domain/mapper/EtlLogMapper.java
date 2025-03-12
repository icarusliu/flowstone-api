package com.liuqi.etl.domain.mapper;

import com.liuqi.common.base.domain.mapper.BaseMapper;
import com.liuqi.etl.bean.dto.EtlLogDTO;
import com.liuqi.etl.bean.query.EtlLogQuery;
import com.liuqi.etl.domain.entity.EtlLogEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * ETL执行日志数据库操作服务 
 * @author Coder Generator 2025-03-10 16:37:56 
 **/
@Mapper
public interface EtlLogMapper extends BaseMapper<EtlLogEntity> {
    List<EtlLogDTO> query(EtlLogQuery query);
}
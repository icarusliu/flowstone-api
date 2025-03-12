package com.liuqi.etl.domain.mapper;

import com.liuqi.common.base.domain.mapper.BaseMapper;
import com.liuqi.etl.bean.dto.EtlJobDependDTO;
import com.liuqi.etl.bean.query.EtlJobDependQuery;
import com.liuqi.etl.domain.entity.EtlJobDependEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 任务依赖数据库操作服务 
 * @author Coder Generator 2025-03-10 17:05:38 
 **/
@Mapper
public interface EtlJobDependMapper extends BaseMapper<EtlJobDependEntity> {
    List<EtlJobDependDTO> query(EtlJobDependQuery query);
}
package com.liuqi.etl.domain.mapper;

import com.liuqi.common.base.domain.mapper.BaseMapper;
import com.liuqi.etl.bean.dto.EtlJobTypeDTO;
import com.liuqi.etl.bean.query.EtlJobTypeQuery;
import com.liuqi.etl.domain.entity.EtlJobTypeEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 任务分类数据库操作服务 
 * @author Coder Generator 2025-03-10 15:10:26 
 **/
@Mapper
public interface EtlJobTypeMapper extends BaseMapper<EtlJobTypeEntity> {
    List<EtlJobTypeDTO> query(EtlJobTypeQuery query);
}
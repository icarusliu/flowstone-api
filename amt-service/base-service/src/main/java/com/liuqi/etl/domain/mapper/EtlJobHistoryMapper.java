package com.liuqi.etl.domain.mapper; /**
 * ETL任务历史记录数据库操作服务 
 * @author Coder Generator 2025-03-16 17:21:59 
 **/

import com.liuqi.common.base.domain.mapper.BaseMapper;
import com.liuqi.etl.domain.entity.EtlJobHistoryEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface EtlJobHistoryMapper extends BaseMapper<EtlJobHistoryEntity> {
}
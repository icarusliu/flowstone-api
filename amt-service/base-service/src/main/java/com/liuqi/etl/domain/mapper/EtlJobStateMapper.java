package com.liuqi.etl.domain.mapper;

import com.liuqi.common.base.domain.mapper.BaseMapper;
import com.liuqi.etl.bean.dto.EtlJobStateDTO;
import com.liuqi.etl.bean.query.EtlJobStateQuery;
import com.liuqi.etl.domain.entity.EtlJobStateEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 执行完成任务记录数据库操作服务 
 * @author Coder Generator 2025-03-10 17:53:08 
 **/
@Mapper
public interface EtlJobStateMapper extends BaseMapper<EtlJobStateEntity> {
    List<EtlJobStateDTO> query(EtlJobStateQuery query);

    void upsert(EtlJobStateDTO dto);
}
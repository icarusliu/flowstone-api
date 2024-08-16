package com.liuqi.dua.domain.mapper; /**
 * 接口执行日志数据库操作服务
 *
 * @author Coder Generator 2024-08-14 14:42:30
 **/

import com.liuqi.common.base.domain.mapper.BaseMapper;
import com.liuqi.dua.domain.entity.ApiLogEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ApiLogMapper extends BaseMapper<ApiLogEntity> {
}
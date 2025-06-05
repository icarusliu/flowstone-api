package com.liuqi.sys.domain.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.liuqi.sys.bean.dto.UserLogDTO;
import com.liuqi.sys.bean.query.UserLogQuery;
import com.liuqi.sys.domain.entity.UserLogEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 用户操作日志数据库操作服务 
 * @author Coder Generator 2025-06-05 11:49:39 
 **/
@Mapper
public interface UserLogMapper extends BaseMapper<UserLogEntity> {
    List<UserLogDTO> query(UserLogQuery query);
}
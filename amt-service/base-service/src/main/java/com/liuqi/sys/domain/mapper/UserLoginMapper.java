package com.liuqi.sys.domain.mapper;

import com.liuqi.common.base.domain.mapper.BaseMapper;
import com.liuqi.sys.bean.dto.UserLoginDTO;
import com.liuqi.sys.bean.query.UserLoginQuery;
import com.liuqi.sys.domain.entity.UserLoginEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户登录日志数据库操作服务 
 * @author Coder Generator 2025-06-05 09:00:03 
 **/
@Mapper
public interface UserLoginMapper extends BaseMapper<UserLoginEntity> {
    List<UserLoginDTO> query(UserLoginQuery query);

    List<UserLoginDTO> userLastLog(@Param("userIds") List<String> userIds);
}
package com.liuqi.sys.domain.mapper;

import com.liuqi.sys.domain.entity.UserEntity;
import com.liuqi.common.base.domain.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<UserEntity> {
}

package com.liuqi.acode.base.domain.mapper;

import com.liuqi.acode.base.domain.entity.UserEntity;
import com.liuqi.common.base.domain.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<UserEntity> {
}

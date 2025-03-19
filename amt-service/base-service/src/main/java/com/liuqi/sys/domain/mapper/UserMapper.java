package com.liuqi.sys.domain.mapper;

import com.liuqi.sys.bean.dto.UserDTO;
import com.liuqi.sys.bean.query.NoRoleUserQuery;
import com.liuqi.sys.domain.entity.UserEntity;
import com.liuqi.common.base.domain.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserMapper extends BaseMapper<UserEntity> {
    /**
     * 查询无角色的用户信息
     */
    List<UserDTO> queryUserNoRole(NoRoleUserQuery query);
}

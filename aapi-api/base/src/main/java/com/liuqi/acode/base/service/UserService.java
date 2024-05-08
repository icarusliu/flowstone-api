package com.liuqi.acode.base.service;

import com.liuqi.acode.base.bean.dto.UserDTO;
import com.liuqi.acode.base.bean.query.UserQuery;
import com.liuqi.acode.base.domain.entity.UserEntity;
import com.liuqi.common.base.service.BaseService;

import java.util.Optional;

public interface UserService extends BaseService<UserEntity, UserDTO, UserQuery> {
    Optional<UserDTO> findByUsername(String username);

    Optional<UserDTO> findByPhone(String phone);

    /**
     * 根据用户名、手机号判断用户是否存在
     */
    boolean userExists(String username, String phon);
}

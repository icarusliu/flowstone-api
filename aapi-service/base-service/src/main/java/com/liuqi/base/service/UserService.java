package com.liuqi.base.service;

import com.liuqi.base.bean.dto.UserDTO;
import com.liuqi.base.bean.query.UserQuery;
import com.liuqi.common.base.service.BaseService;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Optional;

public interface UserService extends BaseService<UserDTO, UserQuery>, UserDetailsService {
    Optional<UserDTO> findByUsername(String username);

    Optional<UserDTO> findByPhone(String phone);

    /**
     * 根据用户名、手机号判断用户是否存在
     */
    boolean userExists(String username, String phon);
}

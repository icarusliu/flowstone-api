package com.liuqi.base.service;

import com.liuqi.base.bean.dto.UserDTO;
import com.liuqi.base.bean.query.UserQuery;
import com.liuqi.common.auth.SimpleUserDetailsService;
import com.liuqi.common.base.service.BaseService;
import com.liuqi.common.utils.UserContext;

import java.util.Optional;

public interface UserService extends BaseService<UserDTO, UserQuery>, SimpleUserDetailsService {
    Optional<UserDTO> findByUsername(String username);

    @Override
    default Optional<UserContext> getUserDetails(String username) {
        return this.findByUsername(username)
                .map(user -> {
                   UserContext userContext = new UserContext(user.getTenantId(), user.getId(), user.getUsername(), user.getNickname(),
                           user.getIsSuperAdmin());
                   userContext.setPassword(user.getPassword());
                   return userContext;
                });
    }

    Optional<UserDTO> findByPhone(String phone);

    /**
     * 根据用户名、手机号判断用户是否存在
     */
    boolean userExists(String username, String phon);
}

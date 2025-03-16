package com.liuqi.base.service;

import com.liuqi.base.bean.dto.UserDTO;
import com.liuqi.base.bean.query.UserQuery;
import com.liuqi.common.base.service.BaseService;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;
import java.util.Optional;

public interface UserService extends BaseService<UserDTO, UserQuery>, UserDetailsService {
    Optional<UserDTO> findByUsername(String username);

    Optional<UserDTO> findByPhone(String phone);

    /**
     * 根据用户名、手机号判断用户是否存在
     */
    boolean userExists(String username, String phon);

    /**
     * 添加用户
     *
     * @param dto     用户信息
     * @param roleIds 用户角色列表
     */
    void insert(UserDTO dto, List<String> roleIds);

    /**
     * 更新用户
     *
     * @param dto     用户信息
     * @param roleIds 角色列表
     */
    void update(UserDTO dto, List<String> roleIds);

    /**
     * 获取用户资源权限列表
     *
     * @param userId 用户id
     * @return 用户资源权限id列表
     */
    List<String> getUserResourceIds(String userId);
}

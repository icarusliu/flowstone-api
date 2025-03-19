package com.liuqi.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.liuqi.common.base.bean.query.DynamicQuery;
import com.liuqi.sys.bean.dto.RoleResourceDTO;
import com.liuqi.sys.bean.dto.UserDTO;
import com.liuqi.sys.bean.dto.UserRoleDTO;
import com.liuqi.sys.bean.enums.UserStatus;
import com.liuqi.sys.bean.query.UserQuery;
import com.liuqi.sys.common.ErrorCodes;
import com.liuqi.sys.domain.entity.UserEntity;
import com.liuqi.sys.domain.mapper.UserMapper;
import com.liuqi.sys.service.*;
import com.liuqi.common.base.service.AbstractBaseService;
import com.liuqi.common.bean.UserContext;
import com.liuqi.common.exception.AppException;
import com.liuqi.common.exception.AuthErrorCodes;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;

import static com.liuqi.sys.common.ErrorCodes.BASE_USER_PHONE_EXISTS;

@Service
public class UserServiceImpl extends AbstractBaseService<UserEntity, UserDTO, UserMapper, UserQuery> implements UserService {
    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private DeptUserService deptUserService;

    @Autowired
    @Lazy
    private PasswordEncoder passwordEncoder;

    @Autowired
    private DeptService deptService;

    @Autowired
    private RoleResourceService roleResourceService;

    @Override
    public UserDTO toDTO(UserEntity entity) {
        UserDTO dto = new UserDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    @Override
    public UserEntity toEntity(UserDTO dto) {
        UserEntity entity = new UserEntity();
        BeanUtils.copyProperties(dto, entity);
        return entity;
    }

    @Override
    protected QueryWrapper<UserEntity> queryToWrapper(UserQuery userQuery) {
        return this.createQueryWrapper()
                .eq(StringUtils.isNotBlank(userQuery.getUsername()), "username", userQuery.getUsername())
                .eq(StringUtils.isNotBlank(userQuery.getPhone()), "phone", userQuery.getPhone())
                .eq(StringUtils.isNotBlank(userQuery.getEmail()), "email", userQuery.getEmail())
                .eq(StringUtils.isNotBlank(userQuery.getDeptId()), "dept_id", userQuery.getDeptId())
                .and(StringUtils.isNotBlank(userQuery.getKey()), wrapper ->
                        wrapper.eq("username", userQuery.getKey())
                                .or(q -> q.eq("phone", userQuery.getKey()))
                                .or(q -> q.eq("email", userQuery.getKey())));
    }

    @Override
    protected boolean processBeforeInsert(UserDTO dto) {
        // 校验用户名、手机号、邮箱是否存在
        String username = dto.getUsername();
        String phone = dto.getPhone();
        if (this.userExists(username, phone)) {
            throw AppException.of(ErrorCodes.BASE_USER_USERNAME_OR_PHONE_EXISTS);
        }

        if (StringUtils.isEmpty(dto.getNickname())) {
            dto.setNickname(dto.getUsername());
        }

        if (StringUtils.isEmpty(dto.getPassword())) {
            dto.setPassword(dto.getUsername());
        }

        dto.setPassword(passwordEncoder.encode(dto.getPassword()));

        dto.setIsSuperAdmin(false);
        dto.setStatus(UserStatus.VALID);

        return true;
    }

    @Override
    protected boolean processBeforeUpdate(UserDTO dto) {
        // 更新前检查手机号是否重复
        String id = dto.getId();
        String phone = dto.getPhone();
        if (!StringUtils.isEmpty(phone)) {
            this.findByPhone(phone).ifPresent(e -> {
                if (!e.getId().equals(id)) {
                    throw AppException.of(BASE_USER_PHONE_EXISTS);
                }
            });
        }

        return true;
    }

    @Override
    protected void processAfterDelete(Collection<String> ids) {
        // 删除用户时需要同时删除相关关联信息
        userRoleService.deleteByUser(ids);
        deptUserService.deleteByUser(ids);
    }

    /**
     * 查询后处理
     *
     * @param list 查询结果
     */
    @Override
    protected void processAfterQuery(List<UserDTO> list) {
        super.processAfterQuery(list);

        if (CollectionUtils.isEmpty(list)) {
            return;
        }

        // 补充用户角色、机构信息
        List<String> deptIds = new ArrayList<>(16);
        List<String> userIds = list.stream()
                .peek(user -> {
                    if (!StringUtils.isEmpty(user.getDeptId())) {
                        deptIds.add(user.getDeptId());
                    }
                })
                .map(UserDTO::getId)
                .toList();
        List<UserRoleDTO> userRoles = userRoleService.findByUsers(userIds);
        Map<String, List<String>> userRoleMap = new HashMap<>(16);
        if (!CollectionUtils.isEmpty(userRoles)) {
            userRoles.forEach(userRole -> {
                String userId = userRole.getUserId();
                userRoleMap.computeIfAbsent(userId, n -> new ArrayList<>(16))
                        .add(userRole.getRoleId());
            });
        }

        // 查询用户机构名称
        Map<String, String> deptNameMap = new HashMap<>(16);
        if (!CollectionUtils.isEmpty(deptIds)) {
            deptService.findByIds(deptIds)
                    .forEach(dept -> deptNameMap.put(dept.getId(), dept.getName()));
        }

        list.forEach(user -> {
            user.setRoleIds(userRoleMap.get(user.getId()));
            if (null != user.getDeptId()) {
                user.setDeptName(deptNameMap.get(user.getDeptId()));
            }
        });
    }

    @Override
    public Optional<UserDTO> findByUsername(String username) {
        UserQuery query = UserQuery.builder()
                .username(username)
                .build();
        return this.query(query).stream().findAny();
    }

    @Override
    public Optional<UserDTO> findByPhone(String phone) {
        UserQuery query = UserQuery.builder()
                .phone(phone)
                .build();
        return this.query(query).stream().findAny();
    }

    /**
     * 根据用户名、手机号判断用户是否存在
     */
    @Override
    public boolean userExists(String username, String phone) {
        QueryWrapper<UserEntity> query = this.createQueryWrapper().eq("username", username)
                .or(q -> q.eq("phone", phone));
        return this.count(query) > 0;
    }

    @Override
    public void insert(UserDTO dto, List<String> roleIds) {
        dto = this.insert(dto);
        String userId = dto.getId();
        userRoleService.addUserRoles(userId, roleIds);
    }

    @Override
    public void update(UserDTO dto, List<String> roleIds) {
        this.update(dto);
        userRoleService.saveUserRoles(dto.getId(), roleIds);
    }

    /**
     * 获取用户资源权限列表
     *
     * @param userId 用户id
     * @return 用户资源权限id列表
     */
    @Override
    public List<String> getUserResourceIds(String userId) {
        // 先获取用户角色，然后根据角色获取授权资源列表
        List<UserRoleDTO> roles = userRoleService.findByUsers(Collections.singletonList(userId));
        if (CollectionUtils.isEmpty(roles)) {
            return new ArrayList<>(0);
        }

        List<String> roleIds = roles.stream().map(UserRoleDTO::getRoleId)
                .toList();
        List<RoleResourceDTO> roleResources = roleResourceService.findByRoles(roleIds);
        return roleResources.stream().map(RoleResourceDTO::getResourceId).toList();
    }

    @Override
    public UserContext loadUserByUsername(String username) throws UsernameNotFoundException {
        return this.findByUsername(username)
                .map(user -> {
                    UserContext userContext = new UserContext();
                    BeanUtils.copyProperties(user, userContext);
                    userContext.setUserId(user.getId());

                    // 补充用户权限相关内容，包括：用户身份及用户角色相关信息
                    List<SimpleGrantedAuthority> authorityList = new ArrayList<>(16);
                    userContext.setAuthorities(authorityList);
                    if (user.getIsSuperAdmin()) {
                        // 兼容hasAuthority及hasRole两种使用方式
                        authorityList.add(new SimpleGrantedAuthority("admin"));
                        authorityList.add(new SimpleGrantedAuthority("ROLE_admin"));
                    } else {
                        authorityList.add(new SimpleGrantedAuthority("user"));
                    }

                    // 补充权限信息 // 暂不实现

                    return userContext;
                }).orElseThrow(() -> AppException.of(AuthErrorCodes.USERNAME_OR_PASSWORD_ERROR));
    }
}

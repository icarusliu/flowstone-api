package com.liuqi.base.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.liuqi.base.service.UserService;
import com.liuqi.base.bean.dto.UserDTO;
import com.liuqi.base.bean.enums.UserStatus;
import com.liuqi.base.bean.query.UserQuery;
import com.liuqi.common.ErrorCodes;
import com.liuqi.base.domain.entity.UserEntity;
import com.liuqi.base.domain.mapper.UserMapper;
import com.liuqi.base.service.DeptUserService;
import com.liuqi.base.service.UserRoleService;
import com.liuqi.common.base.service.AbstractBaseService;
import com.liuqi.common.exception.AppException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;

import static com.liuqi.common.ErrorCodes.BASE_USER_PHONE_EXISTS;

@Service
public class UserServiceImpl extends AbstractBaseService<UserEntity, UserDTO, UserMapper, UserQuery> implements UserService {
    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private DeptUserService deptUserService;

    @Override
    public UserDTO toDTO(UserEntity entity) {
        UserDTO dto = UserDTO.builder().build();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    @Override
    public UserEntity toEntity(UserDTO dto) {
        UserEntity entity = UserEntity.builder().build();
        BeanUtils.copyProperties(dto, entity);
        return entity;
    }

    @Override
    protected QueryWrapper<UserEntity> queryToWrapper(UserQuery userQuery) {
        return this.createQueryWrapper()
                .eq(StringUtils.isNotBlank(userQuery.getUsername()), "username", userQuery.getUsername())
                .eq(StringUtils.isNotBlank(userQuery.getPhone()), "phone", userQuery.getPhone())
                .eq(StringUtils.isNotBlank(userQuery.getEmail()), "email", userQuery.getEmail())
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
            dto.setPassword("abcd@1234");
        }

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
}

package com.liuqi.base.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.Fastjson2TypeHandler;
import com.liuqi.base.bean.enums.UserStatus;
import com.liuqi.common.base.domain.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Data
@ToString
@TableName(value = "base_user", autoResultMap = true)
public class UserEntity extends BaseEntity {
    private String username;

    private String password;

    private String nickname;

    private String phone;

    private String email;

    private UserStatus status;

    private String photo;

    @TableField(typeHandler = Fastjson2TypeHandler.class)
    private Map<String, Object> metadata;

    private String ext1;

    private String ext2;

    private String ext3;

    private Boolean isSuperAdmin;
}

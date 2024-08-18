package com.liuqi.base.bean.dto;

import com.liuqi.base.bean.enums.UserStatus;
import com.liuqi.common.base.bean.dto.BaseDTO;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Data
@ToString
@Builder
public class UserDTO extends BaseDTO {
    private String username;

    private String password;

    private String nickname;

    private String phone;

    private String email;

    private UserStatus status;

    private String photo;

    private Map<String, Object> metadata;

    private String ext1;

    private String ext2;

    private String ext3;

    private Boolean isSuperAdmin;
}

package com.liuqi.sys.bean.dto;

import com.liuqi.sys.bean.enums.UserStatus;
import com.liuqi.common.base.bean.dto.BaseDTO;
import lombok.*;

import java.util.List;
import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Data
@ToString
public class UserDTO extends BaseDTO {
    private String deptId;

    private String deptName;

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

    private List<String> roleIds;
}

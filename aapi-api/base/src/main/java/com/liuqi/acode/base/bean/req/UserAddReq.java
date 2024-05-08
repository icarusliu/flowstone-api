package com.liuqi.acode.base.bean.req;

import com.liuqi.acode.base.bean.enums.UserStatus;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.Map;

@Data
public class UserAddReq {
    @NotBlank(message = "用户名不能为空")
    private String username;

    private String password;

    private String nickname;

    @NotBlank(message = "手机号不能为空")
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

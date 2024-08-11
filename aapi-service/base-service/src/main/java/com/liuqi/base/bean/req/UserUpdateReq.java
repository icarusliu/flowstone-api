package com.liuqi.base.bean.req;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import java.util.Map;

@Data
public class UserUpdateReq {
    @NotBlank(message = "id不能为空")
    private String id;

    private String password;

    private String nickname;

    private String phone;

    private String email;

    private String photo;

    private Map<String, Object> metadata;

    private String ext1;

    private String ext2;

    private String ext3;

    private Boolean isSuperAdmin;
}

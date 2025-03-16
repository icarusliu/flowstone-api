package com.liuqi.sys.bean.req;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class UserUpdateReq {
    private String deptId;

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

    /**
     * 用户角色列表
     */
    private List<String> roleIds;
}

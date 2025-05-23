package com.liuqi.sys.bean.req;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 密码更新
 *
 * @author  LiuQi 2025/4/5-22:08
 * @version V1.0
 **/
@Data
public class PwdUpdateReq {
    @NotBlank
    private String oldPassword;

    @NotBlank
    private String password;
}

package com.liuqi.common.bean;

import lombok.Data;

/**
 * 登录返回结果
 *
 * @author  LiuQi 2024/8/13-23:09
 * @version V1.0
 **/
@Data
public class LoginResp {
    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private UserInfoResp userInfo;
}

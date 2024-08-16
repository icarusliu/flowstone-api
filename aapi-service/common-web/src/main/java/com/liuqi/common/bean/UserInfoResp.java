package com.liuqi.common.bean;

import lombok.Data;

/**
 * 用户信息
 *
 * @author  LiuQi 2024/8/13-23:07
 * @version V1.0
 **/
@Data
public class UserInfoResp {
    private String userId;
    private String username;
    private String nickname;
    private String avatar;
}

package com.liuqi.sys.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.liuqi.common.base.domain.entity.BaseEntity;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户登录日志
 *
 * @author  LiuQi 2025/6/5-8:47
 * @version V1.0
 **/
@Data
@TableName("sys_user_login")
public class UserLoginEntity extends BaseEntity {
    /**
     * 用户名
     */
    private String username;

    /**
     * 登录时间
     */
    private LocalDateTime loginTime;

    /**
     * ip
     */
    private String ip;

    /**
     * 操作系统
     */
    private String os;

    /**
     * 浏览器
     */
    private String browser;
}

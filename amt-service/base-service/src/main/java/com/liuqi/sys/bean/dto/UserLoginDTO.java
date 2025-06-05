package com.liuqi.sys.bean.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.liuqi.common.base.bean.dto.BaseDTO;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户登录日志数据实体 
 * @author Coder Generator 2025-06-05 09:00:03 
 **/
@Data
public class UserLoginDTO extends BaseDTO {
    /**
     * 用户名
     */
    private String username;
    /**
     * 登录时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
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
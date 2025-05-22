package com.liuqi.common.base.bean.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 基础数据类
 *
 * @author  LiuQi 2025/5/21-20:40
 * @version V1.0
 **/
@Data
public class BaseDTO {
    private String id;

    private String tenantId;

    private String createUser;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    private String updateUser;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    private Boolean deleted;
}

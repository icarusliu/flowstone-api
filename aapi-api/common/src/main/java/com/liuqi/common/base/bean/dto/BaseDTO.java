package com.liuqi.common.base.bean.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BaseDTO {
    private String id;

    private String tenantId;

    private String creator;

    private LocalDateTime createTime;

    private String updater;

    private LocalDateTime updateTime;

    private Boolean deleted;
}

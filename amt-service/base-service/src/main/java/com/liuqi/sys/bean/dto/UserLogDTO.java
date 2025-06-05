package com.liuqi.sys.bean.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.liuqi.common.base.bean.dto.BaseDTO;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户操作日志数据实体 
 * @author Coder Generator 2025-06-05 11:49:39 
 **/
@Data
public class UserLogDTO {
    private String id;

    /**
     * 用户id
     */
    private String userId;
    /**
     * 用户名
     */
    private String username;
    /**
     * 模块
     */
    private String module;
    /**
     * 操作类型，CRUD
     */
    private String type;
    /**
     * 参数
     */
    private String params;
    /**
     * IP
     */
    private String ip;
    /**
     * 是否成功
     */
    private Boolean success;
    /**
     * 耗时，ms
     */
    private Integer spentTime;
    /**
     * 结果
     */
    private String result;

    /**
     * 请求路径
     */
    private String path;

    /**
     * 请求方法
     */
    private String method;

    /**
     * 记录创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
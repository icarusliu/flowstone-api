package com.liuqi.sys.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.liuqi.common.base.domain.entity.BaseEntity;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

/**
 * 用户操作日志
 *
 * @author  LiuQi 2025/6/5-11:46
 * @version V1.0
 **/
@Data
@TableName("sys_user_log")
public class UserLogEntity {
    @Id
    @TableId(type = IdType.ASSIGN_ID)
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
     * 请求路径
     */
    private String path;

    /**
     * 请求方法
     */
    private String method;

    /**
     * 结果
     */
    private String result;

    /**
     * 记录创建时间
     */
    private LocalDateTime createTime;
}

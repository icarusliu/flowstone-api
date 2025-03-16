package com.liuqi.dua.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.liuqi.common.base.domain.entity.BaseEntity;
import lombok.Data;

/**
 * 接口历史
 *
 * @author  LiuQi 2025/3/16-17:11
 * @version V1.0
 **/
@Data
@TableName("d_api_history")
public class ApiHistoryEntity extends BaseEntity {
    /**
     * 接口id
     */
    private String apiId;

    /**
     * 是否游客模式可访问
     */
    private Boolean guestMode;

    /**
     * 配置内容
     */
    private String content;

    /**
     * 接口名称
     */
    private String name;

    /**
     * 接口备注
     */
    private String remark;

    /**
     * 接口路径
     */
    private String path;

    /**
     * 接口method
     */
    private String method;

    /**
     * 分类id
     */
    private String typeId;
}

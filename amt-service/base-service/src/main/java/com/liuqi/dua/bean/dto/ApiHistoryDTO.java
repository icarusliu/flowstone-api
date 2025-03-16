package com.liuqi.dua.bean.dto;

import com.liuqi.common.base.bean.dto.BaseDTO;
import lombok.Data;

/**
 * 接口历史数据实体 
 * @author Coder Generator 2025-03-16 17:16:13 
 **/
@Data
public class ApiHistoryDTO extends BaseDTO {
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
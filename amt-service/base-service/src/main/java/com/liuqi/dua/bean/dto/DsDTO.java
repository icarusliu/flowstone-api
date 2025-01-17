package com.liuqi.dua.bean.dto;

import com.liuqi.common.base.bean.dto.BaseDTO;
import lombok.Data;

/**
 * 数据源配置数据实体
 *
 * @author Coder Generator 2024-08-09 11:43:38
 **/
@Data
public class DsDTO extends BaseDTO {
    /**
     * 数据源编码
     */
    private String code;

    /**
     * 数据源名称
     */
    private String name;

    /**
     * 数据源类型
     */
    private String type;

    /**
     * 数据源连接
     */
    private String url;

    /**
     * 数据源用户名
     */
    private String username;

    /**
     * 数据源密码
     */
    private String password;
}
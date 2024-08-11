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
    private String code;
    private String name;
    private String type;
    private String url;
    private String username;
    private String password;
}
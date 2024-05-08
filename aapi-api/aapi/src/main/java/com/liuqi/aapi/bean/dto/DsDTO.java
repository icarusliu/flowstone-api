package com.liuqi.aapi.bean.dto;

import com.liuqi.common.base.bean.dto.BaseDTO;
import lombok.Data;

/** 数据源配置表数据实体 
 * @author Coder Generator 2024-04-20 09:03:05 **/
@Data
public class DsDTO extends BaseDTO {
    private String name;
    private String type;
    private String url;
    private String username;
    private String password;
}
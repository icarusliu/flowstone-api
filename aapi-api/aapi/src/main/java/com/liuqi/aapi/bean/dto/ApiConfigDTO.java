package com.liuqi.aapi.bean.dto;

import com.liuqi.common.base.bean.dto.BaseDTO;
import lombok.Data;

/** API配置表数据实体 
 * @author Coder Generator 2024-04-18 14:25:01 **/
@Data
public class ApiConfigDTO extends BaseDTO {
    private String name;

    private String path;

    private String method;

    private Integer cacheMinute;

    private Integer status;

    private String typeId;

    private String nodes;

    private String outputConfig;

    private String testConfig;

    private String remark;
}
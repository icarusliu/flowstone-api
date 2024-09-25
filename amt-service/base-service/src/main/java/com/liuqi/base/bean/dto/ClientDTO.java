package com.liuqi.base.bean.dto;

import com.liuqi.common.annotations.Comment;
import com.liuqi.common.base.bean.dto.BaseDTO;
import lombok.Data;

/**
 * 客户端数据实体 
 * @author Coder Generator 2024-09-25 09:04:44 
 **/
@Data
public class ClientDTO extends BaseDTO {
    private String name;
    private String secret;
    private Boolean disabled;
    private String remark;
    private String whiteIps;
    private Integer limitInSecond;
    private Boolean withAllApis;
}
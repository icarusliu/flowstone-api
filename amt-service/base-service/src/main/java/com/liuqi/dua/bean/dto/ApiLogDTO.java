package com.liuqi.dua.bean.dto;

import com.liuqi.common.annotations.Comment;
import com.liuqi.common.base.bean.dto.BaseDTO;
import lombok.Data;

/**
 * 接口执行日志数据实体
 *
 * @author Coder Generator 2024-08-14 14:42:30
 **/
@Data
public class ApiLogDTO extends BaseDTO {
    private String apiId;
    private String apiPath;
    private String apiName;
    private int status;
    private String params;
    private String errorMsg;
    private String result;
    private Integer spentTime;
    private String clientId;
    private String clientName;
}
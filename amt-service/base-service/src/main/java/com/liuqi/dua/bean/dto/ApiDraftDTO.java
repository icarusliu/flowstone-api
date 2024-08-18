package com.liuqi.dua.bean.dto;

import com.liuqi.common.base.bean.dto.BaseDTO;
import lombok.Data;

/**
 * 接口草稿数据实体
 *
 * @author Coder Generator 2024-07-08 22:33:46
 **/
@Data
public class ApiDraftDTO extends BaseDTO {
    private String content;
    private String name;
    private String remark;
    private String path;
    private String method;
    private Integer status;
    private String typeId;
    private String typeName;
}
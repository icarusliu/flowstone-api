package com.liuqi.dua.bean.dto;

import com.liuqi.common.base.bean.dto.BaseDTO;
import lombok.Data;

/**
 * 接口数据实体
 *
 * @author Coder Generator 2024-07-08 22:32:36
 **/
@Data
public class ApiDTO extends BaseDTO {
    /**
     * 游客模式
     */
    private Boolean guestMode = false;
    private String content;
    private String name;
    private String remark;
    private String path;
    private String method;
    private String typeId;
    private String typeName;
}
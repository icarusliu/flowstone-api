package com.liuqi.dua.bean.dto;

import com.liuqi.common.base.bean.dto.BaseDTO;
import lombok.Data;

/**
 * 接入方数据实体
 *
 * @author Coder Generator 2024-08-12 19:00:03
 **/
@Data
public class SupplierDTO extends BaseDTO {
    private String name;
    private String url;
    private String remark;
    private String authConfig;
}
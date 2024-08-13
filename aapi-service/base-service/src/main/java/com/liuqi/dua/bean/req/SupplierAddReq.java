package com.liuqi.dua.bean.req;

import lombok.Data;

/**
 * 接入方新增对象
 *
 * @author Coder Generator 2024-08-12 19:00:03
 **/
@Data
public class SupplierAddReq {
    private String name;
    private String url;
    private String remark;
    private String authConfig;
}
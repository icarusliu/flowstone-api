package com.liuqi.dua.bean.req;

import lombok.Data;

/**
 * 数据源配置更新对象
 *
 * @author Coder Generator 2024-08-09 11:43:39
 **/
@Data
public class DsUpdateReq {
    private String id;
    private String code;
    private String name;
    private String type;
    private String url;
    private String username;
    private String password;
}
package com.liuqi.dua.bean.req;

import lombok.Data;

/**
 * 接口分类新增对象
 *
 * @author Coder Generator 2024-08-09 22:08:31
 **/
@Data
public class ApiTypeAddReq {
    private String name;
    private Integer sort;
    private String parentId;
}
package com.liuqi.common.base.bean.query;

import lombok.Data;

import java.util.List;

@Data
public class BaseQuery {
    private String id;

    private List<String> ids;

    private Long pageSize;

    private Long pageNo;
}

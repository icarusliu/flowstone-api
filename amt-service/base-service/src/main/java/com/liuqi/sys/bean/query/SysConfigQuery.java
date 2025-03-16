package com.liuqi.sys.bean.query;

import com.liuqi.common.base.bean.query.BaseQuery;
import lombok.Data;

@Data
public class SysConfigQuery extends BaseQuery {
    private String code;
    private String name;
}
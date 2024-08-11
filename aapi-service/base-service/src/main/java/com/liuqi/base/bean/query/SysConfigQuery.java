package com.liuqi.base.bean.query;

import com.liuqi.common.base.bean.query.BaseQuery;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SysConfigQuery extends BaseQuery {
    private String code;
}
package com.liuqi.acode.base.bean.query;

import com.liuqi.common.base.bean.query.BaseQuery;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DictQuery extends BaseQuery {
    private String key;

    private String code;

    private String name;
}
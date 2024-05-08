package com.liuqi.acode.base.bean.query;

import com.liuqi.common.base.bean.query.BaseQuery;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
public class RoleQuery extends BaseQuery {
    private String code;

    private String name;
}

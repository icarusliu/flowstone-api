package com.liuqi.base.bean.query;

import com.liuqi.common.base.bean.query.BaseQuery;
import lombok.Builder;
import lombok.Data;

import java.util.Collection;

@Data
@Builder
public class DeptQuery extends BaseQuery {
    private String code;

    private String name;

    private String parentId;

    private Collection<String> parentIds;
}
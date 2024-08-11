package com.liuqi.base.bean.query;

import com.liuqi.common.base.bean.query.BaseQuery;
import lombok.Builder;
import lombok.Data;

import java.util.Collection;

@Data
@Builder
public class DictItemQuery extends BaseQuery {
    private String dictId;

    private Collection<String> dictIds;
}
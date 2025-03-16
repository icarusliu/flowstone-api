package com.liuqi.base.bean.query;

import com.liuqi.common.base.bean.query.BaseQuery;
import lombok.Data;

import java.util.List;

@Data
public class RoleResourceQuery extends BaseQuery {
    private String roleId;
    private List<String> roleIds;
}
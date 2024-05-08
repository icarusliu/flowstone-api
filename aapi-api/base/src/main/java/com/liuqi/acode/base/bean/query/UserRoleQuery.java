package com.liuqi.acode.base.bean.query;

import com.liuqi.common.base.bean.query.BaseQuery;
import lombok.Data;

@Data
public class UserRoleQuery extends BaseQuery {
    private String userId;

    private String roleId;
}
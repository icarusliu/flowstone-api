package com.liuqi.base.bean.query;

import com.liuqi.common.base.bean.query.BaseQuery;
import lombok.Data;

import java.util.List;

@Data
public class UserRoleQuery extends BaseQuery {
    private String userId;
    private List<String> userIds;

    private String roleId;
}
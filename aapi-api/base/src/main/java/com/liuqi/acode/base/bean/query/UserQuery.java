package com.liuqi.acode.base.bean.query;

import com.liuqi.common.base.bean.query.BaseQuery;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
public class UserQuery extends BaseQuery {
    /**
     * 关键字，用于根据username/phone/email查询用户
     */
    private String key;
    private String username;
    private String phone;
    private String email;
}

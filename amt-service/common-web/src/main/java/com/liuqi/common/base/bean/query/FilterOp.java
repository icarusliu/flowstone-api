package com.liuqi.common.base.bean.query;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 查询操作类型
 *
 * @author 不空军 15:17
 **/
public enum FilterOp {
    AND("and"),
    OR("or"),
    LIKE("like"),
    EQ("eq"),
    NEQ("neq"),
    IN("in"),
    NULL("null"),
    NOT_NULL("notNull"),
    LT("lt"),
    LE("le"),
    GT("gt"),
    GE("ge"),

    BETWEEN("between");

    FilterOp(String code) {
        this.code = code;
    }

    @JsonValue
    private String code;

    public String getCode() {
        return code;
    }
}

package com.liuqi.dua.bean.dto;

import lombok.Getter;

/**
 * 字段类型
 *
 * @author  LiuQi 2025/3/14-16:12
 * @version V1.0
 **/
@Getter
public enum FieldType {
    VARCHAR("varchar", "字符串"),
    BIGINT("bigint", "整数"),
    NUMERIC("numeric", "小数"),
    DATE("date", "日期"),
    DATETIME("datetime", "日期时间"),
    TIME("time", "时间"),
    ;

    private final String code;
    private final String name;

    public static FieldType parse(String code) {
        for (FieldType value : FieldType.values()) {
            if (value.code.equals(code)) {
                return value;
            }
        }

        return VARCHAR;
    }

    FieldType(String code, String name) {
        this.code = code;
        this.name = name;
    }
}

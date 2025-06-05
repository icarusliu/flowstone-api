package com.liuqi.common.bean;

/**
 * 日志类型
 *
 * @author  LiuQi 2025/6/5-12:13
 * @version V1.0
 **/
public enum LogType {
    NEW("new"),
    EDIT("edit"),
    DELETE("delete"),
    EXPORT("export"),
    IMPORT("import"),
    OTHER("other")
    ;

    private final String code;
    LogType(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}

package com.liuqi.common.exception;

public enum CommErrorCodes implements BaseErrorCodes{
    FIELD_NULL("A01001", "字段不能为空"),

    HTTP_FAILED("A01002", "HTTP请求失败"),

    ;

    private final String code;
    private final String msg;

    CommErrorCodes(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getMsg() {
        return msg;
    }
}

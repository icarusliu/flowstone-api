package com.liuqi.common.exception;

public enum CommErrorCodes implements BaseErrorCodes{
    FIELD_NULL("comm.field.null");

    private final String code;

    CommErrorCodes(String code) {
        this.code = code;
    }

    @Override
    public String getCode() {
        return code;
    }
}

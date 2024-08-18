package com.liuqi.common.exception;

import com.liuqi.common.exception.BaseErrorCodes;

/**
 * 错误信息
 */
public enum AuthErrorCodes implements BaseErrorCodes {
    USERNAME_OR_PASSWORD_ERROR("B01001", "用户名或密码错误"),

    ;

    private final String code;
    private final String msg;

    AuthErrorCodes(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getMsg() {
        return this.msg;
    }


}

package com.liuqi.common.exception;

import java.util.function.Supplier;

/**
 * 应用异常
 */
public class AppException extends RuntimeException{
    private BaseErrorCodes code;
    private Object[] fields;

    private AppException(String msg) {
        super(msg);
    }

    private AppException(BaseErrorCodes code) {
        this.code = code;
    }

    public static AppException of(BaseErrorCodes code, Object...fields) {
        AppException appException = new AppException(code);
        appException.fields = fields;

        return appException;
    }

    public static Supplier<AppException> supplier(BaseErrorCodes code, Object...fields) {
        return () -> AppException.of(code, fields);
    }

    private AppException(Exception ex) {
        super(ex);
    }

    private AppException(String msg, Exception ex) {
        super(msg, ex);
    }

    public BaseErrorCodes getCode() {
        return code;
    }

    public Object[] getFields() {
        return fields;
    }

    /**
     * Returns the detail message string of this throwable.
     *
     * @return the detail message string of this {@code Throwable} instance
     * (which may be {@code null}).
     */
    @Override
    public String getMessage() {
        String msg = code.getMsg();
        if (null == this.fields || 0 == this.fields.length) {
            return msg;
        }
        Map<String, Object> params = new HashMap<>(16);
        for (int i = 0; i < this.fields.length; i++) {
            params.put(String.valueOf(i), this.fields[i]);
        }
        StringSubstitutor sb = new StringSubstitutor(params, "(", ")");
        return sb.replace(msg);
    }
}

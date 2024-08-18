package com.liuqi.common.exception;

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
        return "[" + code.getCode() + "]" + code.getMsg();
    }
}

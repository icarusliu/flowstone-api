package com.liuqi.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletResponse;

/**
 * 应用异常处理器
 */
@RestControllerAdvice
@Slf4j
public class AppExceptionHandler {
    @Autowired
    private HttpServletResponse response;

    @ExceptionHandler(UnauthorizedException.class)
    @ResponseStatus(value = HttpStatus.UNAUTHORIZED, reason = "鉴权失败")
    public void handlerUnauthorizedException() {
    }

    @ExceptionHandler(AppException.class)
    @ResponseBody
    public String handlerAppException(AppException appException) {
        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        return appException.getMessage();
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public String handlerException(Exception exception) {
        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        return exception.getMessage();
    }
}

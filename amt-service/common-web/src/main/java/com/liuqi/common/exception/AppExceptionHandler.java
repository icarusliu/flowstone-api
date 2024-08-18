package com.liuqi.common.exception;

import cn.hutool.core.map.MapBuilder;
import jakarta.el.MethodNotFoundException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

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
    public Map<String, Object> handlerAppException(AppException appException) {
        log.error("业务处理异常", appException);
        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        return MapBuilder.<String, Object>create()
                .put("code", appException.getCode().getCode())
                .put("msg", appException.getMessage())
                .build();
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public String handlerException(Exception exception) {
        log.error("系统异常", exception);
        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        return exception.getMessage();
    }

    @ExceptionHandler(MethodNotFoundException.class)
    @ResponseBody
    public String handlerException(MethodNotFoundException ex) {
        response.setStatus(HttpStatus.NOT_FOUND.value());
        return "404";
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseBody
    public String handlerException(IllegalArgumentException ex) {
        response.setStatus(400);
        return "400";
    }
}

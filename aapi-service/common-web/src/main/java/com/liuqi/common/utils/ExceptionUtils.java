package com.liuqi.common.utils;

import java.util.List;

/**
 * 异常辅助类
 *
 * @author LiuQi 2024/8/14-15:30
 * @version V1.0
 **/
public class ExceptionUtils {
    public static String asString(List<Exception> exceptions) {
        StringBuilder sb = new StringBuilder();
        exceptions.forEach(exception -> sb.append(org.apache.commons.lang3.exception.ExceptionUtils.getStackTrace(exception)));
        return sb.toString();
    }

    public static String asString(Exception ex) {
        return org.apache.commons.lang3.exception.ExceptionUtils.getStackTrace(ex);
    }
}

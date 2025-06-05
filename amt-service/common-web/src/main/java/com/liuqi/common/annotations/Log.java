package com.liuqi.common.annotations;

import com.liuqi.common.bean.LogType;

import java.lang.annotation.*;

/**
 * 日志
 *
 * @author  LiuQi 2025/6/5-12:12
 * @version V1.0
 **/
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Log {
    /**
     * 模块
     */
    String value();

    /**
     * 操作
     * @return 操作
     */
    LogType op();
}

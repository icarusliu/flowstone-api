package com.liuqi.common.annotations;

import java.lang.annotation.*;

/**
 * 无权限控制接口注解
 *
 * @author LiuQi 17:55
 **/
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NoAuth {
}

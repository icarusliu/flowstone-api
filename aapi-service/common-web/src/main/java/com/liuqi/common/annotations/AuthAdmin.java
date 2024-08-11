package com.liuqi.common.annotations;

import java.lang.annotation.*;

/**
 * 管理员鉴权，被注解的方法只有管理员可使用
 *
 * @author LiuQi 18:00
 **/
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AuthAdmin {
}

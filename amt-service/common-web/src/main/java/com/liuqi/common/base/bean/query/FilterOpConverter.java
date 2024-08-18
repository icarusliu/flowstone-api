package com.liuqi.common.base.bean.query;

import cn.hutool.core.convert.TypeConverter;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Type;

/**
 * 
 *
 * @author  LiuQi 2024/8/11-17:38
 * @version V1.0
 **/
@Slf4j
public class FilterOpConverter implements TypeConverter {
    @Override
    public Object convert(Type type, Object o) {
        log.debug("test, {}", type);

        return o;
    }
}

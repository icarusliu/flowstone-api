package com.liuqi.common;

import com.liuqi.dua.bean.dto.ApiDTO;
import jakarta.el.MethodNotFoundException;

/**
 * API信息存储
 *
 * @author  LiuQi 2024/9/25-18:40
 * @version V1.0
 **/
public class ApiHolder {
    private static final ThreadLocal<ApiDTO> holder = new ThreadLocal<>();

    public static void set(ApiDTO apiInfo) {
        holder.set(apiInfo);
    }

    public static ApiDTO get() {
        return holder.get();
    }

    public static ApiDTO getOrThrow() {
        ApiDTO dto = holder.get();
        if (null == dto) {
            throw new MethodNotFoundException();
        }

        return dto;
    }
}

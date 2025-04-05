package com.liuqi.common.utils;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Optional;

/**
 * WEB辅助类
 *
 * @author LiuQi 17:13
 **/
@Slf4j
public class WebUtils {
    /**
     * 解析请求中的token
     * @param request 请求信息
     * @return 解析到的token
     */
    public static Optional<String> getToken(HttpServletRequest request) {
        if (null == request.getCookies()) {
            return Optional.ofNullable(request.getHeader("authorization"))
                    .map(str -> str.replace("Bearer ", ""));
        }

        String token = Arrays.stream(request.getCookies())
                .filter(cookie -> cookie.getName().equals("access_token"))
                .findFirst()
                .map(Cookie::getValue).orElseGet(() -> {
                    // 如果cookie中没有，则从authorization中获取
                    return Optional.ofNullable(request.getHeader("authorization"))
                            .map(str -> str.replace("Bearer ", ""))
                            .orElse(null);
                });

        return Optional.ofNullable(token);
    }

    /**
     * 获取请求ip
     *
     * @param request 请求
     * @return ip
     */
    public static String getRequestHost(HttpServletRequest request) {
        String ip = request.getHeader("X-Real-IP");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Forwarded-For");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    /**
     * 文件下载
     *
     * @param response 请求返回
     * @param fileName 文件名
     * @param bytes    文件内容
     */
    public static void downloadFile(HttpServletResponse response, String fileName, byte[] bytes, String mimeType) {
        if (StringUtils.isBlank(mimeType)) {
            response.setContentType("application/x-zip-compressed");
        } else {
            response.setContentType(mimeType);
        }

        response.setHeader("Content-Disposition", "attachment; filename=" + fileName);

        OutputStream outputStream = null;
        try {
            outputStream = response.getOutputStream();
            IOUtils.write(bytes, outputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (null != outputStream) {
                    outputStream.flush();
                    outputStream.close();
                }
            } catch (IOException ignored) {
            }
        }
    }
}

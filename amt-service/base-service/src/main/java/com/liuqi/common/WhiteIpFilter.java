package com.liuqi.common;

import com.liuqi.common.utils.WebUtils;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 白名单ip过滤器
 * 只过滤发布接口访问请求
 *
 * @author LiuQi 2024/9/24-18:39
 * @version V1.0
 **/
@Component
@Slf4j
public class WhiteIpFilter implements Filter {
    @Value("${api.whiteIps}")
    private String whiteIps;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        // 只处理已发布接口的调用请求
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        String url = request.getRequestURI();
        boolean matched = url.startsWith("/dua/");

        if (StringUtils.isBlank(whiteIps) || "*".equals(whiteIps) || !matched) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        String ip = WebUtils.getRequestHost(request);
        if (ip.equals("0:0:0:0:0:0:0:1") || ip.equals("127.0.0.1")) {
            // 本机
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        String[] ipArr = ip.split("\\.");
        boolean find = false;
        for (String s : whiteIps.split(",")) {
            find = this.ipMatches(ipArr, s);
            if (find) {
                break;
            }
        }

        if (find) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        // 未匹配上
        log.error("ip({})未在白名单中", ip);
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        response.setStatus(401);
    }

    /**
     * ip与对应串是否匹配
     * @param ipArr ip使用,分隔后的数组
     * @param matchStr 匹配串，比如*.*.*.*或者0.0.0.0
     * @return 是否匹配
     */
    private boolean ipMatches(String[] ipArr, String matchStr) {
        String[] matchArr = matchStr.split("\\.");

        if (matchArr.length != 4) {
            return false;
        }

        for (int i = 0; i < matchArr.length; i++) {
            String match = matchArr[i];
            if (match.equals("0") || match.equals("*")) {
                continue;
            }

            String ipSplit = ipArr[i];
            if (ipSplit.equals(match)) {
                continue;
            }

            return false;
        }

        return true;
    }
}

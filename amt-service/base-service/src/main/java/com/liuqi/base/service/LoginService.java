package com.liuqi.base.service;

import com.alibaba.fastjson2.JSON;
import com.liuqi.base.bean.dto.ClientDTO;
import com.liuqi.common.ErrorCodes;
import com.liuqi.common.bean.LoginResp;
import com.liuqi.common.bean.UserInfoResp;
import com.liuqi.common.exception.AppException;
import com.liuqi.common.utils.AuthUtils;
import com.liuqi.common.bean.UserContext;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 * Security登录服务
 *
 * @author LiuQi 2024/8/15-10:19
 * @version V1.0
 **/
@Slf4j
@Service
public class LoginService {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private HttpServletResponse response;

    @Autowired
    private ClientService clientService;

    @Value("${spring.security.domain:ngq.com}")
    private String domain;

    /**
     * 用户登录
     *
     * @param username 用户名
     * @param password 密码
     * @return 登录结果，包括token及用户信息
     */
    public LoginResp login(String username, String password) {
        Authentication authentication = UsernamePasswordAuthenticationToken.unauthenticated(username, password);
        Authentication resp = authenticationManager.authenticate(authentication);
        log.info("登录结果: {}", JSON.toJSONString(resp));
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(resp);
        SecurityContextHolder.setContext(securityContext);

        UserContext userContext = (UserContext) resp.getPrincipal();
        String token = AuthUtils.generateToken(userContext);

        // 设置Cookie
        Cookie cookie = new Cookie("access_token", token);
        cookie.setDomain(domain);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        response.addCookie(cookie);

        UserInfoResp userInfo = new UserInfoResp();
        BeanUtils.copyProperties(userContext, userInfo);
        LoginResp result = new LoginResp();
        result.setAccessToken(token);
        result.setUserInfo(userInfo);

        return result;
    }

    /**
     * 客户端登录
     *
     * @param clientId     客户端id
     * @param clientSecret 客户端密钥
     * @return 登录结果
     */
    public String loginByClient(String clientId, String clientSecret) {
        ClientDTO client = clientService.findById(clientId).orElseThrow(() -> AppException.of(ErrorCodes.BASE_CLIENT_INVALID));
        if (!client.getSecret().equals(clientSecret)) {
            throw AppException.of(ErrorCodes.BASE_CLIENT_INVALID);
        }

        if (client.getDisabled()) {
            throw AppException.of(ErrorCodes.BASE_CLIENT_DISABLED);
        }

        UserContext userContext = new UserContext();
        userContext.setUserId(client.getId());
        userContext.setUsername(client.getId());
        userContext.setNickname(client.getName());
        userContext.setIsClient(true);

        Authentication authentication = UsernamePasswordAuthenticationToken.authenticated(userContext, null, userContext.getAuthorities());
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);

        return AuthUtils.generateToken(userContext);
    }
}

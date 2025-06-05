package com.liuqi.sys.service;

import cn.hutool.http.useragent.UserAgent;
import cn.hutool.http.useragent.UserAgentUtil;
import com.alibaba.fastjson2.JSON;
import com.liuqi.common.bean.UserContextHolder;
import com.liuqi.common.utils.WebUtils;
import com.liuqi.sys.bean.dto.ClientDTO;
import com.liuqi.common.bean.LoginResp;
import com.liuqi.common.bean.UserInfoResp;
import com.liuqi.common.exception.AppException;
import com.liuqi.common.utils.AuthUtils;
import com.liuqi.common.bean.UserContext;
import com.liuqi.sys.bean.dto.UserLoginDTO;
import com.liuqi.sys.common.ErrorCodes;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

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

    @Value("${spring.security.timeout:30}")
    private Integer timeoutInMinute;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private UserLoginEntityService userLoginEntityService;

    @Autowired
    private HttpServletRequest request;

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
        UserContextHolder.set(userContext);

        // 设置Cookie
        Cookie cookie = new Cookie("access_token", token);
        cookie.setDomain(domain);
        cookie.setPath("/");
        cookie.setHttpOnly(true);

        // 设置cookie过期时间，此处过期时间设置长一些，通过Redis控制token的过期与延长
        cookie.setMaxAge(20 * 24 * 60 * 60);
        redisTemplate.opsForValue().set("user-" + userContext.getUserId(), "1", timeoutInMinute, TimeUnit.MINUTES);

        response.addCookie(cookie);

        UserInfoResp userInfo = new UserInfoResp();
        BeanUtils.copyProperties(userContext, userInfo);
        LoginResp result = new LoginResp();
        result.setAccessToken(token);
        result.setUserInfo(userInfo);

        // 记录用户登录日志
        UserLoginDTO userLoginDTO = new UserLoginDTO();
        userLoginDTO.setUsername(username);
        userLoginDTO.setLoginTime(LocalDateTime.now());
        UserAgent userAgent = WebUtils.getUserAgent(request);
        userLoginDTO.setBrowser(userAgent.getBrowser().getName());
        userLoginDTO.setOs(userAgent.getOs().getName());
        userLoginDTO.setIp(WebUtils.getRequestHost(request));
        userLoginEntityService.insert(userLoginDTO);

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

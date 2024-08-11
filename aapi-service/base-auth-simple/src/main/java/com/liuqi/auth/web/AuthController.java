package com.liuqi.auth.web;

import cn.hutool.core.map.MapBuilder;
import com.liuqi.auth.service.LoginService;
import com.liuqi.common.annotations.NoAuth;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 登录控制器
 *
 * @author LiuQi 15:27
 **/
@RestController
@RequestMapping("/auth")
@Slf4j
public class AuthController {
    @Autowired
    private LoginService loginService;

    @GetMapping("login")
    @NoAuth
    public Map<String, Object> login(String username, String password) {
        return MapBuilder.<String, Object>create()
                .put("accessToken", loginService.login(username, password))
                .build();
    }
}

package com.liuqi.base.web;

import com.liuqi.common.bean.LoginResp;
import com.liuqi.base.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 登录控制器
 *
 * @author  LiuQi 2024/8/15-10:18
 * @version V1.0
 **/
@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private LoginService loginService;

    @GetMapping("login")
    public LoginResp login(String username, String password) {
        return loginService.login(username, password);
    }
}

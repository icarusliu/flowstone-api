package com.liuqi.sys.web;

import com.liuqi.sys.bean.dto.UserDTO;
import com.liuqi.sys.bean.resp.InitInfo;
import com.liuqi.sys.service.MenuService;
import com.liuqi.sys.service.UserService;
import com.liuqi.common.bean.UserContextHolder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 初始化控制器
 *
 * @author  LiuQi 2024/10/4-9:27
 * @version V1.0
 **/
@RestController
@RequestMapping("/init")
public class InitController {
    @Autowired
    private UserService userService;

    @Autowired
    private MenuService menuService;

    @GetMapping("info")
    public InitInfo getInitInfo() {
        InitInfo initInfo = new InitInfo();

        String userId = UserContextHolder.getUserId().orElse("");
        UserDTO userInfo = UserDTO.builder().build();
        if (!StringUtils.isEmpty(userId)) {
            userInfo = userService.findById(userId).orElse(userInfo);
        }
        userInfo.setPassword(null);
        initInfo.setUserInfo(userInfo);

        // 获取菜单信息
        initInfo.setMenuTree(menuService.getTree(false, true));

        return initInfo;
    }
}

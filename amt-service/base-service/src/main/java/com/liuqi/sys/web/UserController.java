package com.liuqi.sys.web;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.liuqi.sys.bean.dto.UserDTO;
import com.liuqi.sys.bean.dto.UserLoginDTO;
import com.liuqi.sys.bean.query.NoRoleUserQuery;
import com.liuqi.sys.bean.query.UserQuery;
import com.liuqi.sys.bean.req.PwdUpdateReq;
import com.liuqi.sys.bean.req.UserAddReq;
import com.liuqi.sys.bean.req.UserUpdateReq;
import com.liuqi.sys.manager.UserManager;
import com.liuqi.sys.service.UserService;
import com.liuqi.common.bean.UserContextHolder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户控制器
 *
 * @author 不空军 2023/12/27 15:03
 **/
@RestController
@RequestMapping("/sys/user")
@Slf4j
@Tag(name = "用户控制器")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private UserManager userManager;

    @PostMapping("add")
    @Operation(summary = "新增")
    public void add(@RequestBody @Validated UserAddReq req) {
        UserDTO dto = new UserDTO();
        BeanUtils.copyProperties(req, dto);
        userService.insert(dto, req.getRoleIds());
    }

    @PutMapping("update")
    @Operation(summary = "更新")
    public void update(@RequestBody @Validated UserUpdateReq req) {
        UserDTO dto = new UserDTO();
        BeanUtils.copyProperties(req, dto);
        userService.update(dto, req.getRoleIds());
    }

    @DeleteMapping("delete/{id}")
    @Operation(summary = "删除")
    public void delete(@PathVariable("id") String id) {
        userService.delete(id);
    }

    @PostMapping("page-query")
    @Operation(summary = "查询-分页")
    public IPage<UserDTO> pageQuery(@RequestBody UserQuery query) {
        return userService.pageQuery(query);
    }

    @PostMapping("query")
    @Operation(summary = "查询-不分页")
    public List<UserDTO> query(@RequestBody UserQuery query) {
        return userService.query(query);
    }

    @GetMapping("info")
    @Operation(summary = "当前登录用户信息")
    public UserDTO getCurrentUserInfo() {
        String userId = UserContextHolder.getUserId().orElse("");
        if (StringUtils.isEmpty(userId)) {
            return new UserDTO();
        }
        UserDTO user = userService.findById(userId).orElse(new UserDTO());
        user.setPassword(null);
        return user;
    }

    /**
     * 根据角色查用户
     */
    @GetMapping("query-by-role")
    public List<UserDTO> findByRole(String roleId) {
        return userManager.getUserByRole(roleId);
    }

    @PostMapping("query-no-role")
    public IPage<UserDTO> findNoRoleUsers(@RequestBody NoRoleUserQuery query) {
        return userManager.queryUserNoRole(query);
    }

    @PostMapping("update-mine")
    public void updateMyInfo(@RequestBody UserUpdateReq req) {
        String userId = UserContextHolder.getUserIdOrThrow();
        UserDTO dto = new UserDTO();
        BeanUtils.copyProperties(req, dto);
        dto.setId(userId);
        userService.update(dto);
    }

    @PostMapping("update-pwd")
    public void updatePwd(@RequestBody @Validated PwdUpdateReq req) {
        userManager.updatePwd(req);
    }

    @PostMapping("online/page-query")
    public IPage<UserLoginDTO> getOnlineUsers(@RequestBody UserQuery query) {
        return userService.queryOnlineUsers(query);
    }
}

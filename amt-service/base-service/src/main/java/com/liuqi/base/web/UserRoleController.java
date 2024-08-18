package com.liuqi.base.web;

import com.liuqi.base.service.UserRoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

@RestController
@RequestMapping("/base/user-role")
@Slf4j
@Tag(name = "控制器")
public class UserRoleController {
    @Autowired
    private UserRoleService userRoleService;

    @GetMapping("/add/{userId}")
    public void addUserRole(@PathVariable("userId") String userId,
                            @RequestParam("roleIds") String roleIds) {
        userRoleService.addUserRoles(userId, Arrays.asList(roleIds.split(",")));
    }

    @DeleteMapping("/delete/{userId}")
    @Operation(description = "删除用户角色")
    public void deleteUserRole(@PathVariable("userId") String userId,
                               @RequestParam("roleIds") String roleIds) {
        userRoleService.deleteUserRoles(userId, Arrays.asList(roleIds.split(",")));
    }

    @GetMapping("/save/{userId}")
    @Operation(description = "保存用户角色，不存在的进行删除")
    public void saveUserRoles(@PathVariable("userId") String userId,
                              @RequestParam("roleIds") String roleIds) {
        userRoleService.saveUserRoles(userId, Arrays.asList(roleIds.split(",")));
    }
}
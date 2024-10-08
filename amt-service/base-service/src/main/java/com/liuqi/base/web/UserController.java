package com.liuqi.base.web;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.liuqi.base.bean.dto.UserDTO;
import com.liuqi.base.bean.query.UserQuery;
import com.liuqi.base.bean.req.UserAddReq;
import com.liuqi.base.bean.req.UserUpdateReq;
import com.liuqi.base.service.UserService;
import com.liuqi.common.base.bean.query.DynamicQuery;
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
@RequestMapping("/base/user")
@Slf4j
@Tag(name = "用户控制器")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("add")
    @Operation(summary = "新增")
    public void add(@RequestBody @Validated UserAddReq req) {
        UserDTO dto = UserDTO.builder().build();
        BeanUtils.copyProperties(req, dto);
        userService.insert(dto);
       
    }

    @PutMapping("update")
    @Operation(summary = "更新")
    public void update(@RequestBody @Validated UserUpdateReq req) {
        UserDTO dto = UserDTO.builder().build();
        BeanUtils.copyProperties(req, dto);
        userService.update(dto);
       
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

    @PostMapping("filter")
    @Operation(summary = "查询-动态")
    public IPage<UserDTO> pageQuery(@RequestBody DynamicQuery query) {
        return userService.dynamicQuery(query);
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
            return UserDTO.builder().build();
        }
        UserDTO user = userService.findById(userId).orElse(UserDTO.builder().build());
        user.setPassword(null);
        return user;
    }
}

package com.liuqi.sys.web;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.liuqi.sys.bean.dto.UserLoginDTO;
import com.liuqi.sys.bean.query.UserLoginQuery;
import com.liuqi.sys.service.UserLoginEntityService;
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
 * 用户登录日志控制器 
 * @author Coder Generator 2025-06-05 09:00:04 
 **/
@RestController
@RequestMapping("/base/user-login")
@Slf4j
@Tag(name = "用户登录日志控制器")
public class UserLoginController {
    @Autowired
    private UserLoginEntityService service;

    @PostMapping("add")
    @Operation(summary = "新增")
    public void add(@RequestBody @Validated UserLoginDTO req) {
        service.insert(req);
    }

    @PutMapping("update")
    @Operation(summary = "更新")
    public void update(@RequestBody @Validated UserLoginDTO req) {
        service.update(req);
    }

    @PostMapping("save")
    @Operation(summary = "保存")
    public UserLoginDTO save(@RequestBody @Validated UserLoginDTO req) {
        if (StringUtils.isBlank(req.getId())) {
            return service.insert(req);
        }
        service.update(req);
        return req;
    }

    @DeleteMapping("delete/{id}")
    @Operation(summary = "删除")
    public void delete(@PathVariable("id") String id) {
        service.delete(id);
    }

    @GetMapping("detail/{id}")
    @Operation(summary = "根据id查找记录")
    public UserLoginDTO findById(@PathVariable("id") String id) {
        return service.findById(id).orElse(null);
    }

    @PostMapping("page-query")
    @Operation(summary = "查询-分页")
    public IPage<UserLoginDTO> pageQuery(@RequestBody UserLoginQuery query) {
        return service.pageQuery(query);
    }

    @PostMapping("query")
    @Operation(summary = "查询-不分页")
    public List<UserLoginDTO> query(@RequestBody UserLoginQuery query) {
        return service.query(query);
    }
}
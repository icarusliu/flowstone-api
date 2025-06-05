package com.liuqi.sys.web;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.liuqi.sys.bean.dto.UserLogDTO;
import com.liuqi.sys.bean.query.UserLogQuery;
import com.liuqi.sys.service.UserLogEntityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * 用户操作日志控制器 
 * @author Coder Generator 2025-06-05 11:49:39 
 **/
@RestController
@RequestMapping("/base/user-log")
@Slf4j
@Tag(name = "用户操作日志控制器")
public class UserLogController {
    @Autowired
    private UserLogEntityService service;

    @PostMapping("add")
    @Operation(summary = "新增")
    public void add(@RequestBody @Validated UserLogDTO req) {
        service.insert(req);
    }

    @PutMapping("update")
    @Operation(summary = "更新")
    public void update(@RequestBody @Validated UserLogDTO req) {
        service.update(req);
    }

    @PostMapping("save")
    @Operation(summary = "保存")
    public UserLogDTO save(@RequestBody @Validated UserLogDTO req) {
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
    public UserLogDTO findById(@PathVariable("id") String id) {
        return service.findById(id).orElse(null);
    }

    @PostMapping("page-query")
    @Operation(summary = "查询-分页")
    public IPage<UserLogDTO> pageQuery(@RequestBody UserLogQuery query) {
        query.setExcludeFields(Arrays.asList("params", "result"));
        return service.pageQuery(query);
    }
}
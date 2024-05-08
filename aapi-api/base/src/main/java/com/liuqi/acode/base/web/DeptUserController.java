package com.liuqi.acode.base.web;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.liuqi.acode.base.bean.query.DeptUserQuery;
import com.liuqi.acode.base.service.DeptUserService;
import com.liuqi.acode.base.bean.dto.DeptUserDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/base/dept-user")
@Slf4j
@Tag(name = "控制器")
public class DeptUserController {
    @Autowired
    private DeptUserService service;

    @GetMapping("add/{deptId}")
    @Operation(summary = "新增")
    public Mono<Void> add(@PathVariable("deptId") String deptId,
                          @RequestParam("userIds") String userIdsStr) {
        List<String> userIds = Arrays.asList(userIdsStr.split(","));
        List<DeptUserDTO> dtos = userIds.stream().map(userId -> {
            DeptUserDTO dto = new DeptUserDTO();
            dto.setUserId(userId);
            dto.setDeptId(deptId);
            return dto;
        }).collect(Collectors.toList());
        service.insert(dtos);

        return Mono.empty();
    }

    @DeleteMapping("delete/{deptId}")
    @Operation(summary = "删除")
    public Mono<Void> delete(@PathVariable("deptId") String deptId,
                             @RequestParam(value = "userIds", required = false) String userIds) {
        if (StringUtils.isEmpty(userIds)) {
            service.deleteByDept(Collections.singleton(deptId));
        } else {
            service.deleteDeptUsers(deptId, Arrays.asList(userIds.split(",")));
        }
        return Mono.empty();
    }

    @PostMapping("page-query")
    @Operation(summary = "查询-分页")
    public IPage<DeptUserDTO> pageQuery(@RequestBody DeptUserQuery query) {
        return service.pageQuery(query);
    }

    @PostMapping("query")
    @Operation(summary = "查询-不分页")
    public List<DeptUserDTO> query(@RequestBody DeptUserQuery query) {
        return service.query(query);
    }
}
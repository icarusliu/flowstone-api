package com.liuqi.base.web;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.liuqi.base.bean.dto.RoleDTO;
import com.liuqi.base.bean.query.RoleQuery;
import com.liuqi.base.bean.req.RoleAddReq;
import com.liuqi.base.bean.req.RoleUpdateReq;
import com.liuqi.base.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/base/role")
@Slf4j
@Tag(name = "角色控制器")
public class RoleController {
    @Autowired
    private RoleService roleService;

    @PostMapping("add")
    @Operation(summary = "新增")
    public Mono<Void> add(@RequestBody @Validated RoleAddReq req) {
        RoleDTO dto = RoleDTO.builder().build();
        BeanUtils.copyProperties(req, dto);
        roleService.insert(dto);
        return Mono.empty();
    }

    @PutMapping("update")
    @Operation(summary = "更新")
    public Mono<Void> update(@RequestBody @Validated RoleUpdateReq req) {
        RoleDTO dto = RoleDTO.builder().build();
        BeanUtils.copyProperties(req, dto);
        roleService.update(dto);
        return Mono.empty();
    }

    @DeleteMapping("delete/{id}")
    @Operation(summary = "删除")
    public Mono<Void> delete(@PathVariable("id") String id) {
        roleService.delete(id);
        return Mono.empty();
    }

    @PostMapping("page-query")
    @Operation(summary = "查询-分页")
    public IPage<RoleDTO> pageQuery(@RequestBody RoleQuery query) {
        return roleService.pageQuery(query);
    }

    @PostMapping("query")
    @Operation(summary = "查询-不分页")
    public List<RoleDTO> query(@RequestBody RoleQuery query) {
        return roleService.query(query);
    }
}

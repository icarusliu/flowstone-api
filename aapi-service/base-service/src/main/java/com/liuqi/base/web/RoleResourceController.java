package com.liuqi.base.web;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.liuqi.base.bean.query.RoleResourceQuery;
import com.liuqi.base.bean.req.RoleResourceUpdateReq;
import com.liuqi.base.service.RoleResourceService;
import com.liuqi.base.bean.dto.RoleResourceDTO;
import com.liuqi.base.bean.req.RoleResourceAddReq;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/base/role-resource")
@Slf4j
@Tag(name = "控制器")
public class RoleResourceController {
    @Autowired
    private RoleResourceService service;

    @PostMapping("add")
    @Operation(summary = "新增")
    public Mono<Void> add(@RequestBody RoleResourceAddReq req) {
        RoleResourceDTO dto = new RoleResourceDTO();
        BeanUtils.copyProperties(req, dto);
        service.insert(dto);
        return Mono.empty();
    }

    @PutMapping("update")
    @Operation(summary = "更新")
    public Mono<Void> update(@RequestBody RoleResourceUpdateReq req) {
        RoleResourceDTO dto = new RoleResourceDTO();
        BeanUtils.copyProperties(req, dto);
        service.update(dto);
        return Mono.empty();
    }

    @DeleteMapping("delete/{id}")
    @Operation(summary = "删除")
    public Mono<Void> delete(@PathVariable("id") String id) {
        service.delete(id);
        return Mono.empty();
    }

    @PostMapping("page-query")
    @Operation(summary = "查询-分页")
    public IPage<RoleResourceDTO> pageQuery(@RequestBody RoleResourceQuery query) {
        return service.pageQuery(query);
    }

    @PostMapping("query")
    @Operation(summary = "查询-不分页")
    public List<RoleResourceDTO> query(@RequestBody RoleResourceQuery query) {
        return service.query(query);
    }
}
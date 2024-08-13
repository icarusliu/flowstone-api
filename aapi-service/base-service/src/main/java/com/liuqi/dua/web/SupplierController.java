package com.liuqi.dua.web;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.liuqi.common.base.bean.query.DynamicQuery;
import com.liuqi.dua.bean.dto.SupplierDTO;
import com.liuqi.dua.bean.query.SupplierQuery;
import com.liuqi.dua.bean.req.SupplierAddReq;
import com.liuqi.dua.bean.req.SupplierUpdateReq;
import com.liuqi.dua.service.SupplierService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * 接入方控制器
 *
 * @author Coder Generator 2024-08-12 19:00:03
 **/
@RestController
@RequestMapping("/base/supplier")
@Slf4j
@Tag(name = "控制器")
public class SupplierController {
    @Autowired
    private SupplierService service;

    @PostMapping("add")
    @Operation(summary = "新增")
    public Mono<Void> add(@RequestBody @Validated SupplierAddReq req) {
        SupplierDTO dto = new SupplierDTO();
        BeanUtils.copyProperties(req, dto);
        service.insert(dto);
        return Mono.empty();
    }

    @PutMapping("update")
    @Operation(summary = "更新")
    public Mono<Void> update(@RequestBody @Validated SupplierUpdateReq req) {
        SupplierDTO dto = new SupplierDTO();
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

    @GetMapping("detail/{id}")
    @Operation(summary = "根据id查找记录")
    public Mono<SupplierDTO> findById(@PathVariable("id") String id) {
        return Mono.justOrEmpty(service.findById(id).orElse(null));
    }

    @PostMapping("page-query")
    @Operation(summary = "查询-分页")
    public IPage<SupplierDTO> pageQuery(@RequestBody SupplierQuery query) {
        return service.pageQuery(query);
    }

    @PostMapping("filter")
    @Operation(summary = "查询-动态")
    public IPage<SupplierDTO> pageQuery(@RequestBody DynamicQuery query) {
        return service.dynamicQuery(query);
    }

    @PostMapping("query")
    @Operation(summary = "查询-不分页")
    public List<SupplierDTO> query(@RequestBody SupplierQuery query) {
        return service.query(query);
    }
}
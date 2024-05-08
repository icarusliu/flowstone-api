package com.liuqi.aapi.web;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.liuqi.aapi.service.DsService;
import com.liuqi.aapi.bean.dto.DsDTO;
import com.liuqi.aapi.bean.query.DsQuery;
import com.liuqi.aapi.bean.req.DsAddReq;
import com.liuqi.aapi.bean.req.DsUpdateReq;
import com.liuqi.common.base.bean.query.DynamicQuery;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

/** 数据源配置表控制器 
 * @author Coder Generator 2024-04-20 09:03:05 **/
@RestController
@RequestMapping("/ds")
@Slf4j
@Tag(name = "控制器")
public class DsController {
    @Autowired
    private DsService service;

    @PostMapping("add")
    @Operation(summary = "新增")
    public Mono<Void> add(@RequestBody @Validated DsAddReq req) {
        DsDTO dto = new DsDTO();
        BeanUtils.copyProperties(req, dto);
        service.insert(dto);
        return Mono.empty();
    }

    @PutMapping("update")
    @Operation(summary = "更新")
    public Mono<Void> update(@RequestBody @Validated DsUpdateReq req) {
        DsDTO dto = new DsDTO();
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
    public IPage<DsDTO> pageQuery(@RequestBody DsQuery query) {
        return service.pageQuery(query);
    }

    @PostMapping("filter")
    @Operation(summary = "查询-动态")
    public IPage<DsDTO> pageQuery(@RequestBody DynamicQuery query) {
        return service.dynamicQuery(query);
    }

    @PostMapping("query")
    @Operation(summary = "查询-不分页")
    public List<DsDTO> query(@RequestBody DsQuery query) {
        return service.query(query);
    }
}
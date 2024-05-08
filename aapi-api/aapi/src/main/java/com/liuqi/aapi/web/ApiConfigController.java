package com.liuqi.aapi.web;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.liuqi.aapi.service.ApiConfigService;
import com.liuqi.aapi.bean.dto.ApiConfigDTO;
import com.liuqi.aapi.bean.query.ApiConfigQuery;
import com.liuqi.aapi.bean.req.ApiConfigAddReq;
import com.liuqi.aapi.bean.req.ApiConfigUpdateReq;
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

/** API配置表控制器 
 * @author Coder Generator 2024-04-18 14:25:01 **/
@RestController
@RequestMapping("/api-config")
@Slf4j
@Tag(name = "控制器")
public class ApiConfigController {
    @Autowired
    private ApiConfigService service;

    @PostMapping("add")
    @Operation(summary = "新增")
    public Mono<Void> add(@RequestBody @Validated ApiConfigAddReq req) {
        ApiConfigDTO dto = new ApiConfigDTO();
        BeanUtils.copyProperties(req, dto);
        service.insert(dto);
        return Mono.empty();
    }

    @PutMapping("update")
    @Operation(summary = "更新")
    public Mono<Void> update(@RequestBody @Validated ApiConfigUpdateReq req) {
        ApiConfigDTO dto = new ApiConfigDTO();
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
    public IPage<ApiConfigDTO> pageQuery(@RequestBody ApiConfigQuery query) {
        return service.pageQuery(query);
    }

    @PostMapping("filter")
    @Operation(summary = "查询-动态")
    public IPage<ApiConfigDTO> pageQuery(@RequestBody DynamicQuery query) {
        return service.dynamicQuery(query);
    }

    @PostMapping("query")
    @Operation(summary = "查询-不分页")
    public List<ApiConfigDTO> query(@RequestBody ApiConfigQuery query) {
        return service.query(query);
    }
}
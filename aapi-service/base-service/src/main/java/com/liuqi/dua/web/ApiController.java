package com.liuqi.dua.web;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.liuqi.common.base.bean.query.DynamicQuery;
import com.liuqi.dua.bean.dto.ApiDTO;
import com.liuqi.dua.bean.dto.ApiTypeDTO;
import com.liuqi.dua.bean.query.ApiQuery;
import com.liuqi.dua.bean.req.ApiAddReq;
import com.liuqi.dua.bean.req.ApiUpdateReq;
import com.liuqi.dua.service.ApiService;
import com.liuqi.dua.service.ApiTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 接口控制器
 *
 * @author Coder Generator 2024-07-08 22:32:36
 **/
@RestController
@RequestMapping("/base/api")
@Slf4j
@Tag(name = "控制器")
public class ApiController {
    @Autowired
    private ApiService service;

    @Autowired
    private ApiTypeService typeService;

    @PostMapping("add")
    @Operation(summary = "新增")
    public Mono<Void> add(@RequestBody @Validated ApiAddReq req) {
        ApiDTO dto = new ApiDTO();
        BeanUtils.copyProperties(req, dto);
        service.insert(dto);
        return Mono.empty();
    }

    @PutMapping("update")
    @Operation(summary = "更新")
    public Mono<Void> update(@RequestBody @Validated ApiUpdateReq req) {
        ApiDTO dto = new ApiDTO();
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
    public Mono<ApiDTO> findById(@PathVariable("id") String id) {
        return Mono.justOrEmpty(service.findById(id).orElse(null));
    }

    @PostMapping("page-query")
    @Operation(summary = "查询-分页")
    public IPage<ApiDTO> pageQuery(@RequestBody ApiQuery query) {
        IPage<ApiDTO> page = service.pageQuery(query);

        // 需要补充分类名称
        if (page.getTotal() == 0) {
            return page;
        }

        List<String> typeIds = page.getRecords().stream().map(ApiDTO::getTypeId)
                .toList();
        Map<String, String> typeMap = typeService.findByIds(typeIds)
                .stream()
                .collect(Collectors.toMap(ApiTypeDTO::getId, ApiTypeDTO::getName));
        page.getRecords().forEach(item -> item.setTypeName(typeMap.get(item.getTypeId())));

        return page;
    }

    @PostMapping("filter")
    @Operation(summary = "查询-动态")
    public IPage<ApiDTO> pageQuery(@RequestBody DynamicQuery query) {
        return service.dynamicQuery(query);
    }

    @PostMapping("query")
    @Operation(summary = "查询-不分页")
    public List<ApiDTO> query(@RequestBody ApiQuery query) {
        return service.query(query);
    }
}
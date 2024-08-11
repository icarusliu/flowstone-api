package com.liuqi.dua.web;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.liuqi.common.base.bean.query.DynamicQuery;
import com.liuqi.dua.bean.dto.ApiDraftDTO;
import com.liuqi.dua.bean.dto.ApiTypeDTO;
import com.liuqi.dua.bean.query.ApiDraftQuery;
import com.liuqi.dua.bean.req.ApiDraftAddReq;
import com.liuqi.dua.bean.req.ApiDraftUpdateReq;
import com.liuqi.dua.service.ApiDraftService;
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
 * 接口草稿控制器
 *
 * @author Coder Generator 2024-07-08 22:33:46
 **/
@RestController
@RequestMapping("/base/api-draft")
@Slf4j
@Tag(name = "控制器")
public class ApiDraftController {
    @Autowired
    private ApiDraftService service;

    @Autowired
    private ApiTypeService apiTypeService;

    @PostMapping("add")
    @Operation(summary = "新增")
    public Mono<ApiDraftDTO> add(@RequestBody @Validated ApiDraftAddReq req) {
        ApiDraftDTO dto = new ApiDraftDTO();
        BeanUtils.copyProperties(req, dto);
        dto.setStatus(0);
        return Mono.just(service.insert(dto));
    }

    @PutMapping("update")
    @Operation(summary = "更新")
    public Mono<Void> update(@RequestBody @Validated ApiDraftUpdateReq req) {
        ApiDraftDTO dto = new ApiDraftDTO();
        BeanUtils.copyProperties(req, dto);
        if (dto.getStatus() == 1) {
            dto.setStatus(2);
        }
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
    public Mono<ApiDraftDTO> findById(@PathVariable("id") String id) {
        return Mono.justOrEmpty(service.findById(id).orElse(null));
    }

    @PostMapping("page-query")
    @Operation(summary = "查询-分页")
    public IPage<ApiDraftDTO> pageQuery(@RequestBody ApiDraftQuery query) {
        IPage<ApiDraftDTO> page = service.pageQuery(query);

        // 需要补充分类名称
        if (page.getTotal() == 0) {
            return page;
        }

        List<String> typeIds = page.getRecords().stream().map(ApiDraftDTO::getTypeId)
                .toList();
        Map<String, String> typeMap = apiTypeService.findByIds(typeIds)
                .stream()
                .collect(Collectors.toMap(ApiTypeDTO::getId, ApiTypeDTO::getName));
        page.getRecords().forEach(item -> item.setTypeName(typeMap.get(item.getTypeId())));

        return page;
    }

    @PostMapping("filter")
    @Operation(summary = "查询-动态")
    public IPage<ApiDraftDTO> pageQuery(@RequestBody DynamicQuery query) {
        return service.dynamicQuery(query);
    }

    @PostMapping("query")
    @Operation(summary = "查询-不分页")
    public List<ApiDraftDTO> query(@RequestBody ApiDraftQuery query) {
        return service.query(query);
    }

    @GetMapping("publish/{id}")
    @Operation(summary = "发布接口")
    public void publish(@PathVariable String id) {
        service.publish(id);
    }

    @GetMapping("offline/{id}")
    @Operation(summary = "下线接口")
    public void offline(@PathVariable String id) {
        service.offline(id);
    }
}
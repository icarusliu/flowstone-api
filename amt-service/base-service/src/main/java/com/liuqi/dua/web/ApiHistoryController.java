package com.liuqi.dua.web;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.liuqi.dua.bean.dto.ApiHistoryDTO;
import com.liuqi.dua.bean.query.ApiHistoryQuery;
import com.liuqi.dua.service.ApiHistoryEntityService;
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
 * 接口历史控制器 
 * @author Coder Generator 2025-05-23 08:52:24 
 **/
@RestController
@RequestMapping("/base/api-history")
@Slf4j
@Tag(name = "接口历史控制器")
public class ApiHistoryController {
    @Autowired
    private ApiHistoryEntityService service;

    @PostMapping("add")
    @Operation(summary = "新增")
    public void add(@RequestBody @Validated ApiHistoryDTO req) {
        service.insert(req);
    }

    @PutMapping("update")
    @Operation(summary = "更新")
    public void update(@RequestBody @Validated ApiHistoryDTO req) {
        service.update(req);
    }

    @PostMapping("save")
    @Operation(summary = "保存")
    public ApiHistoryDTO save(@RequestBody @Validated ApiHistoryDTO req) {
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
    public ApiHistoryDTO findById(@PathVariable("id") String id) {
        return service.findById(id).orElse(null);
    }

    @PostMapping("page-query")
    @Operation(summary = "查询-分页")
    public IPage<ApiHistoryDTO> pageQuery(@RequestBody ApiHistoryQuery query) {
        return service.pageQuery(query);
    }

    @PostMapping("query")
    @Operation(summary = "查询-不分页")
    public List<ApiHistoryDTO> query(@RequestBody ApiHistoryQuery query) {
        return service.query(query);
    }
}
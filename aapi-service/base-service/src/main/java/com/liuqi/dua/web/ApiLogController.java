package com.liuqi.dua.web;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.liuqi.common.base.bean.query.DynamicQuery;
import com.liuqi.dua.bean.dto.ApiLogDTO;
import com.liuqi.dua.bean.query.ApiLogQuery;
import com.liuqi.dua.bean.req.ApiLogAddReq;
import com.liuqi.dua.bean.req.ApiLogUpdateReq;
import com.liuqi.dua.service.ApiLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 接口执行日志控制器
 *
 * @author Coder Generator 2024-08-14 14:42:30
 **/
@RestController
@RequestMapping("/base/api-log")
@Slf4j
@Tag(name = "控制器")
public class ApiLogController {
    @Autowired
    private ApiLogService service;

    @PostMapping("add")
    @Operation(summary = "新增")
    public void add(@RequestBody @Validated ApiLogAddReq req) {
        ApiLogDTO dto = new ApiLogDTO();
        BeanUtils.copyProperties(req, dto);
        service.insert(dto);
       
    }

    @PutMapping("update")
    @Operation(summary = "更新")
    public void update(@RequestBody @Validated ApiLogUpdateReq req) {
        ApiLogDTO dto = new ApiLogDTO();
        BeanUtils.copyProperties(req, dto);
        service.update(dto);
       
    }

    @DeleteMapping("delete/{id}")
    @Operation(summary = "删除")
    public void delete(@PathVariable("id") String id) {
        service.delete(id);
       
    }

    @GetMapping("detail/{id}")
    @Operation(summary = "根据id查找记录")
    public ApiLogDTO findById(@PathVariable("id") String id) {
        return service.findById(id).orElse(null);
    }

    @PostMapping("page-query")
    @Operation(summary = "查询-分页")
    public IPage<ApiLogDTO> pageQuery(@RequestBody ApiLogQuery query) {
        return service.pageQuery(query);
    }

    @PostMapping("filter")
    @Operation(summary = "查询-动态")
    public IPage<ApiLogDTO> pageQuery(@RequestBody DynamicQuery query) {
        return service.dynamicQuery(query);
    }

    @PostMapping("query")
    @Operation(summary = "查询-不分页")
    public List<ApiLogDTO> query(@RequestBody ApiLogQuery query) {
        return service.query(query);
    }
}
package com.liuqi.etl.web;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.liuqi.common.base.bean.query.DynamicQuery;
import com.liuqi.etl.bean.dto.EtlLogDTO;
import com.liuqi.etl.bean.query.EtlLogQuery;
import com.liuqi.etl.bean.req.EtlLogAddReq;
import com.liuqi.etl.bean.req.EtlLogUpdateReq;
import com.liuqi.etl.service.EtlLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * ETL执行日志控制器 
 * @author Coder Generator 2025-03-10 16:37:56 
 **/
@RestController
@RequestMapping("/etl/log")
@Slf4j
@Tag(name = "ETL执行日志控制器")
public class EtlLogController {
    @Autowired
    private EtlLogService service;

    @PostMapping("add")
    @Operation(summary = "新增")
    public void add(@RequestBody @Validated EtlLogAddReq req) {
        EtlLogDTO dto = new EtlLogDTO();
        BeanUtils.copyProperties(req, dto);
        service.insert(dto);
    }

    @PutMapping("update")
    @Operation(summary = "更新")
    public void update(@RequestBody @Validated EtlLogUpdateReq req) {
        EtlLogDTO dto = new EtlLogDTO();
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
    public EtlLogDTO findById(@PathVariable("id") String id) {
        return service.findById(id).orElse(null);
    }

    @PostMapping("page-query")
    @Operation(summary = "查询-分页")
    public IPage<EtlLogDTO> pageQuery(@RequestBody EtlLogQuery query) {
        return service.pageQuery(query);
    }

    @PostMapping("filter")
    @Operation(summary = "查询-动态")
    public IPage<EtlLogDTO> pageQuery(@RequestBody DynamicQuery query) {
        return service.dynamicPageQuery(query);
    }

    @PostMapping("query")
    @Operation(summary = "查询-不分页")
    public List<EtlLogDTO> query(@RequestBody EtlLogQuery query) {
        return service.query(query);
    }
}
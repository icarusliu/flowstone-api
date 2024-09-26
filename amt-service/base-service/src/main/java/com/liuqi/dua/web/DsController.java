package com.liuqi.dua.web;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.liuqi.common.base.bean.query.DynamicQuery;
import com.liuqi.dua.bean.dto.DsDTO;
import com.liuqi.dua.bean.query.DsQuery;
import com.liuqi.dua.bean.req.DsAddReq;
import com.liuqi.dua.bean.req.DsUpdateReq;
import com.liuqi.dua.service.DsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 数据源配置控制器
 *
 * @author Coder Generator 2024-08-09 11:43:39
 **/
@RestController
@RequestMapping("/base/ds")
@Slf4j
@Tag(name = "控制器")
public class DsController {
    @Autowired
    private DsService service;

    @PostMapping("add")
    @Operation(summary = "新增")
    public void add(@RequestBody @Validated DsAddReq req) {
        DsDTO dto = new DsDTO();
        BeanUtils.copyProperties(req, dto);
        service.insert(dto);
    }

    @PutMapping("update")
    @Operation(summary = "更新")
    public void update(@RequestBody @Validated DsUpdateReq req) {
        DsDTO dto = new DsDTO();
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
    public DsDTO findById(@PathVariable("id") String id) {
        return service.findById(id).orElse(null);
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

    @GetMapping("tables")
    @Operation(summary = "获取表清单")
    public List<String> getTables(@RequestParam("ds") String ds) {
        return service.getTables(ds);
    }

    @GetMapping("table-fields")
    @Operation(summary = "查询表列清单")
    public List<String> getTableFields(String ds, String table) {
        return service.getTableFields(ds, table);
    }

    @PostMapping("test")
    public void test(@RequestBody DsDTO ds) {
        service.testConnect(ds);
    }
}
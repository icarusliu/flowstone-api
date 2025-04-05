package com.liuqi.etl.web;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.liuqi.common.base.bean.query.DynamicQuery;
import com.liuqi.common.utils.TreeUtils;
import com.liuqi.etl.bean.dto.EtlJobTypeDTO;
import com.liuqi.etl.bean.query.EtlJobTypeQuery;
import com.liuqi.etl.bean.req.EtlJobTypeAddReq;
import com.liuqi.etl.bean.req.EtlJobTypeUpdateReq;
import com.liuqi.etl.service.EtlJobTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 任务分类控制器 
 * @author Coder Generator 2025-03-10 15:10:27 
 **/
@RestController
@RequestMapping("/etl/job-type")
@Slf4j
@Tag(name = "任务分类控制器")
public class EtlJobTypeController {
    @Autowired
    private EtlJobTypeService service;

    @PostMapping("add")
    @Operation(summary = "新增")
    public void add(@RequestBody @Validated EtlJobTypeAddReq req) {
        EtlJobTypeDTO dto = new EtlJobTypeDTO();
        BeanUtils.copyProperties(req, dto);
        service.insert(dto);
    }

    @PutMapping("update")
    @Operation(summary = "更新")
    public void update(@RequestBody @Validated EtlJobTypeUpdateReq req) {
        EtlJobTypeDTO dto = new EtlJobTypeDTO();
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
    public EtlJobTypeDTO findById(@PathVariable("id") String id) {
        return service.findById(id).orElse(null);
    }

    @PostMapping("page-query")
    @Operation(summary = "查询-分页")
    public IPage<EtlJobTypeDTO> pageQuery(@RequestBody EtlJobTypeQuery query) {
        return service.pageQuery(query);
    }

    @PostMapping("filter")
    @Operation(summary = "查询-动态")
    public IPage<EtlJobTypeDTO> pageQuery(@RequestBody DynamicQuery query) {
        return service.dynamicPageQuery(query);
    }

    @PostMapping("query")
    @Operation(summary = "查询-不分页")
    public List<EtlJobTypeDTO> query(@RequestBody EtlJobTypeQuery query) {
        return service.query(query);
    }

    @GetMapping("tree")
    public List<EtlJobTypeDTO> tree() {
        return TreeUtils.toTree(service.findAll());
    }
}
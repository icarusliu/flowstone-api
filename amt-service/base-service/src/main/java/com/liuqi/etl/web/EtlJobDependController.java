package com.liuqi.etl.web;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.liuqi.common.base.bean.query.DynamicQuery;
import com.liuqi.etl.bean.dto.EtlJobDependDTO;
import com.liuqi.etl.bean.query.EtlJobDependQuery;
import com.liuqi.etl.bean.req.EtlJobDependAddReq;
import com.liuqi.etl.bean.req.EtlJobDependUpdateReq;
import com.liuqi.etl.service.EtlJobDependService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 任务依赖控制器 
 * @author Coder Generator 2025-03-10 17:05:53 
 **/
@RestController
@RequestMapping("/etl/job-depend")
@Slf4j
@Tag(name = "任务依赖控制器")
public class EtlJobDependController {
    @Autowired
    private EtlJobDependService service;

    @PostMapping("add")
    @Operation(summary = "新增")
    public void add(@RequestBody @Validated EtlJobDependAddReq req) {
        EtlJobDependDTO dto = new EtlJobDependDTO();
        BeanUtils.copyProperties(req, dto);
        service.insert(dto);
    }

    @DeleteMapping("delete/{id}")
    @Operation(summary = "删除")
    public void delete(@PathVariable("id") String id) {
        service.delete(id);
    }

    @GetMapping("detail/{id}")
    @Operation(summary = "根据id查找记录")
    public EtlJobDependDTO findById(@PathVariable("id") String id) {
        return service.findById(id).orElse(null);
    }

    @PostMapping("page-query")
    @Operation(summary = "查询-分页")
    public IPage<EtlJobDependDTO> pageQuery(@RequestBody EtlJobDependQuery query) {
        return service.pageQuery(query);
    }

    @PostMapping("filter")
    @Operation(summary = "查询-动态")
    public IPage<EtlJobDependDTO> pageQuery(@RequestBody DynamicQuery query) {
        return service.dynamicPageQuery(query);
    }

    @PostMapping("query")
    @Operation(summary = "查询-不分页")
    public List<EtlJobDependDTO> query(@RequestBody EtlJobDependQuery query) {
        return service.query(query);
    }

    @PutMapping("update")
    public void updateJobDepend(@RequestBody EtlJobDependUpdateReq req) {
        service.update(req.getJobId(), req.getDependJobIds());
    }
}
package com.liuqi.etl.web;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.liuqi.common.base.bean.query.DynamicQuery;
import com.liuqi.etl.bean.dto.EtlJobStateDTO;
import com.liuqi.etl.bean.query.EtlJobStateQuery;
import com.liuqi.etl.bean.req.EtlJobStateAddReq;
import com.liuqi.etl.bean.req.EtlJobStateUpdateReq;
import com.liuqi.etl.service.EtlJobStateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 执行完成任务记录控制器 
 * @author Coder Generator 2025-03-10 17:53:08 
 **/
@RestController
@RequestMapping("/base/etl-job-finished")
@Slf4j
@Tag(name = "执行完成任务记录控制器")
public class EtlJobFinishedController {
    @Autowired
    private EtlJobStateService service;

    @PostMapping("add")
    @Operation(summary = "新增")
    public void add(@RequestBody @Validated EtlJobStateAddReq req) {
        EtlJobStateDTO dto = new EtlJobStateDTO();
        BeanUtils.copyProperties(req, dto);
        service.insert(dto);
    }

    @PutMapping("update")
    @Operation(summary = "更新")
    public void update(@RequestBody @Validated EtlJobStateUpdateReq req) {
        EtlJobStateDTO dto = new EtlJobStateDTO();
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
    public EtlJobStateDTO findById(@PathVariable("id") String id) {
        return service.findById(id).orElse(null);
    }

    @PostMapping("page-query")
    @Operation(summary = "查询-分页")
    public IPage<EtlJobStateDTO> pageQuery(@RequestBody EtlJobStateQuery query) {
        return service.pageQuery(query);
    }

    @PostMapping("filter")
    @Operation(summary = "查询-动态")
    public IPage<EtlJobStateDTO> pageQuery(@RequestBody DynamicQuery query) {
        return service.dynamicQuery(query);
    }

    @PostMapping("query")
    @Operation(summary = "查询-不分页")
    public List<EtlJobStateDTO> query(@RequestBody EtlJobStateQuery query) {
        return service.query(query);
    }
}
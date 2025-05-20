package com.liuqi.etl.web;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.liuqi.etl.bean.dto.EtlJobHistoryDTO;
import com.liuqi.etl.bean.query.EtlJobHistoryQuery;
import com.liuqi.etl.service.EtlJobHistoryService;
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
 * ETL任务历史记录控制器 
 * @author Coder Generator 2025-03-16 17:21:59 
 **/
@RestController
@RequestMapping("/base/etl-job-history")
@Slf4j
@Tag(name = "ETL任务历史记录控制器")
public class EtlJobHistoryController {
    @Autowired
    private EtlJobHistoryService service;

    @PostMapping("add")
    @Operation(summary = "新增")
    public void add(@RequestBody @Validated EtlJobHistoryDTO req) {
        service.insert(req);
    }

    @PutMapping("update")
    @Operation(summary = "更新")
    public void update(@RequestBody @Validated EtlJobHistoryDTO req) {
        service.update(req);
    }

    @PostMapping("save")
    @Operation(summary = "保存")
    public EtlJobHistoryDTO save(@RequestBody @Validated EtlJobHistoryDTO req) {
        if (StringUtils.isNotBlank(req.getId())) {
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
    public EtlJobHistoryDTO findById(@PathVariable("id") String id) {
        return service.findById(id).orElse(null);
    }

    @PostMapping("page-query")
    @Operation(summary = "查询-分页")
    public IPage<EtlJobHistoryDTO> pageQuery(@RequestBody EtlJobHistoryQuery query) {
        return service.pageQuery(query);
    }

    @PostMapping("query")
    @Operation(summary = "查询-不分页")
    public List<EtlJobHistoryDTO> query(@RequestBody EtlJobHistoryQuery query) {
        return service.query(query);
    }
}
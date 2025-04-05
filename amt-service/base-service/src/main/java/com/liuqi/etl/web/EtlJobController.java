package com.liuqi.etl.web;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.liuqi.common.base.bean.query.DynamicQuery;
import com.liuqi.etl.bean.dto.EtlJobDTO;
import com.liuqi.etl.bean.dto.EtlJobHistoryDTO;
import com.liuqi.etl.bean.query.EtlJobHistoryQuery;
import com.liuqi.etl.bean.query.EtlJobQuery;
import com.liuqi.etl.bean.req.EtlJobAddReq;
import com.liuqi.etl.bean.req.EtlJobUpdateReq;
import com.liuqi.etl.bean.resp.BloodTree;
import com.liuqi.etl.service.EtlJobHistoryService;
import com.liuqi.etl.service.EtlJobService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * ETL任务控制器 
 * @author Coder Generator 2025-03-10 16:36:11 
 **/
@RestController
@RequestMapping("/etl/job")
@Slf4j
@Tag(name = "ETL任务控制器")
public class EtlJobController {
    @Autowired
    private EtlJobService service;

    @Autowired
    private EtlJobHistoryService historyService;

    @PostMapping("add")
    @Operation(summary = "新增")
    public EtlJobDTO add(@RequestBody @Validated EtlJobAddReq req) {
        EtlJobDTO dto = new EtlJobDTO();
        BeanUtils.copyProperties(req, dto);
        return service.insert(dto);
    }

    @PutMapping("update")
    @Operation(summary = "更新")
    public void update(@RequestBody @Validated EtlJobUpdateReq req) {
        EtlJobDTO dto = new EtlJobDTO();
        BeanUtils.copyProperties(req, dto);
        if (null == dto.getVersion()) {
            dto.setVersion(1);
        } else {
            dto.setVersion(dto.getVersion() + 1);
        }
        service.update(dto);
    }

    @DeleteMapping("delete/{id}")
    @Operation(summary = "删除")
    public void delete(@PathVariable("id") String id) {
        service.delete(id);
    }

    @GetMapping("detail/{id}")
    @Operation(summary = "根据id查找记录")
    public EtlJobDTO findById(@PathVariable("id") String id) {
        return service.findById(id).orElse(null);
    }

    @PostMapping("page-query")
    @Operation(summary = "查询-分页")
    public IPage<EtlJobDTO> pageQuery(@RequestBody EtlJobQuery query) {
        return service.pageQuery(query);
    }

    @PostMapping("filter")
    @Operation(summary = "查询-动态")
    public IPage<EtlJobDTO> pageQuery(@RequestBody DynamicQuery query) {
        return service.dynamicPageQuery(query);
    }

    @PostMapping("query")
    @Operation(summary = "查询-不分页")
    public List<EtlJobDTO> query(@RequestBody EtlJobQuery query) {
        return service.query(query);
    }

    /**
     * 自动解析任务依赖
     * 只解析直接的任务依赖，间接的不处理
     * @param jobId 任务id
     * @return 任务依赖的其它任务
     */
    @GetMapping("parse-depend")
    public List<EtlJobDTO> parseDepends(String jobId) {
        return service.parseDepends(jobId);
    }

    @GetMapping("publish")
    public void publish(String jobId) {
        service.publish(jobId);
    }

    @GetMapping("offline")
    public void offline(String jobId) {
        service.offline(jobId);
    }

    @GetMapping("history")
    public List<EtlJobHistoryDTO> getHistory(String id) {
        EtlJobHistoryQuery query = new EtlJobHistoryQuery();
        query.setJobId(id);
        return historyService.query(query);
    }

    @GetMapping("blood-tree")
    public List<BloodTree> getJobBlook(String id) {
        return service.getBloodRelation(id);
    }
}
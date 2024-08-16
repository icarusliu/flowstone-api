package com.liuqi.base.web;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.liuqi.base.bean.dto.ScheduleTaskDTO;
import com.liuqi.base.bean.query.ScheduleTaskQuery;
import com.liuqi.base.bean.req.ScheduleTaskAddReq;
import com.liuqi.base.bean.req.ScheduleTaskUpdateReq;
import com.liuqi.base.service.ScheduleTaskExecutor;
import com.liuqi.base.service.ScheduleTaskService;
import com.liuqi.common.base.bean.query.DynamicQuery;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 定时任务控制器
 *
 * @author Coder Generator 2024-08-16 15:54:28
 **/
@RestController
@RequestMapping("/base/schedule-task")
@Slf4j
@Tag(name = "控制器")
public class ScheduleTaskController {
    @Autowired
    private ScheduleTaskService service;

    @Autowired
    private ScheduleTaskExecutor executor;

    @GetMapping("start/{id}")
    public void startTask(@PathVariable String id) {
        executor.startTask(id);
    }

    @GetMapping("stop/{id}")
    public void stopTask(@PathVariable String id) {
        executor.stopTask(id);
    }

    @GetMapping("restart/{id}")
    public void restartTask(@PathVariable String id) {
        executor.restartTask(id);
    }

    @PostMapping("add")
    @Operation(summary = "新增")
    public void add(@RequestBody @Validated ScheduleTaskAddReq req) {
        ScheduleTaskDTO dto = new ScheduleTaskDTO();
        BeanUtils.copyProperties(req, dto);
        service.insert(dto);
    }

    @PutMapping("update")
    @Operation(summary = "更新")
    public void update(@RequestBody @Validated ScheduleTaskUpdateReq req) {
        ScheduleTaskDTO dto = new ScheduleTaskDTO();
        BeanUtils.copyProperties(req, dto);
        service.update(dto);

        executor.restartTask(req.getId());
    }

    @GetMapping("invalid/{id}")
    public void setInvalid(@PathVariable("id") String id) {
        executor.stopTask(id);
        ScheduleTaskDTO dto = new ScheduleTaskDTO();
        dto.setId(id);
        dto.setStatus(0);
        service.update(dto);
    }

    @GetMapping("valid/{id}")
    public void setValid(@PathVariable("id") String id) {
        ScheduleTaskDTO dto = new ScheduleTaskDTO();
        dto.setId(id);
        dto.setStatus(1);
        service.update(dto);
        executor.startTask(id);
    }

    @DeleteMapping("delete/{id}")
    @Operation(summary = "删除")
    public void delete(@PathVariable("id") String id) {
        executor.stopTask(id);
        service.delete(id);
    }

    @GetMapping("detail/{id}")
    @Operation(summary = "根据id查找记录")
    public ScheduleTaskDTO findById(@PathVariable("id") String id) {
        return service.findById(id).orElse(null);
    }

    @PostMapping("page-query")
    @Operation(summary = "查询-分页")
    public IPage<ScheduleTaskDTO> pageQuery(@RequestBody ScheduleTaskQuery query) {
        return service.pageQuery(query);
    }

    @PostMapping("filter")
    @Operation(summary = "查询-动态")
    public IPage<ScheduleTaskDTO> pageQuery(@RequestBody DynamicQuery query) {
        return service.dynamicQuery(query);
    }

    @PostMapping("query")
    @Operation(summary = "查询-不分页")
    public List<ScheduleTaskDTO> query(@RequestBody ScheduleTaskQuery query) {
        return service.query(query);
    }
}
package com.liuqi.dua.web;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.liuqi.common.base.bean.query.DynamicQuery;
import com.liuqi.dua.bean.dto.ModelDTO;
import com.liuqi.dua.bean.query.ModelQuery;
import com.liuqi.dua.bean.req.ModelAddReq;
import com.liuqi.dua.bean.req.ModelUpdateReq;
import com.liuqi.dua.service.ModelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 模型控制器 
 * @author Coder Generator 2025-03-14 12:45:23 
 **/
@RestController
@RequestMapping("/dua/model")
@Slf4j
@Tag(name = "模型控制器")
public class ModelController {
    @Autowired
    private ModelService service;

    @PostMapping("add")
    @Operation(summary = "新增")
    public ModelDTO add(@RequestBody @Validated ModelAddReq req) {
        return service.insert(req);
    }

    @PutMapping("update")
    @Operation(summary = "更新")
    public void update(@RequestBody @Validated ModelUpdateReq req) {
        service.update(req);
    }

    @DeleteMapping("delete/{id}")
    @Operation(summary = "删除")
    public void delete(@PathVariable("id") String id) {
        service.delete(id);
    }

    @GetMapping("detail/{id}")
    @Operation(summary = "根据id查找记录")
    public ModelDTO findById(@PathVariable("id") String id) {
        return service.findById(id).orElse(null);
    }

    @PostMapping("page-query")
    @Operation(summary = "查询-分页")
    public IPage<ModelDTO> pageQuery(@RequestBody ModelQuery query) {
        return service.pageQuery(query);
    }

    @PostMapping("filter")
    @Operation(summary = "查询-动态")
    public IPage<ModelDTO> pageQuery(@RequestBody DynamicQuery query) {
        return service.dynamicQuery(query);
    }

    @PostMapping("query")
    @Operation(summary = "查询-不分页")
    public List<ModelDTO> query(@RequestBody ModelQuery query) {
        return service.query(query);
    }

    @GetMapping("publish")
    public void publish(String id) {
        service.publish(id);
    }

    @GetMapping("offline")
    public void offline(String id) {
        service.offline(id);
    }
}
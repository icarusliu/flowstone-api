package com.liuqi.dua.web;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.liuqi.common.base.bean.query.DynamicQuery;
import com.liuqi.dua.bean.dto.ModelFieldDTO;
import com.liuqi.dua.bean.query.ModelFieldQuery;
import com.liuqi.dua.bean.req.ModelFieldAddReq;
import com.liuqi.dua.bean.req.ModelFieldUpdateReq;
import com.liuqi.dua.service.ModelFieldService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 模型字段控制器 
 * @author Coder Generator 2025-03-14 12:59:51 
 **/
@RestController
@RequestMapping("/dua/model-field")
@Slf4j
@Tag(name = "模型字段控制器")
public class ModelFieldController {
    @Autowired
    private ModelFieldService service;

    @PostMapping("add")
    @Operation(summary = "新增")
    public void add(@RequestBody @Validated ModelFieldAddReq req) {
        ModelFieldDTO dto = new ModelFieldDTO();
        BeanUtils.copyProperties(req, dto);
        service.insert(dto);
    }

    @PutMapping("update")
    @Operation(summary = "更新")
    public void update(@RequestBody @Validated ModelFieldUpdateReq req) {
        ModelFieldDTO dto = new ModelFieldDTO();
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
    public ModelFieldDTO findById(@PathVariable("id") String id) {
        return service.findById(id).orElse(null);
    }

    @PostMapping("page-query")
    @Operation(summary = "查询-分页")
    public IPage<ModelFieldDTO> pageQuery(@RequestBody ModelFieldQuery query) {
        return service.pageQuery(query);
    }

    @PostMapping("filter")
    @Operation(summary = "查询-动态")
    public IPage<ModelFieldDTO> pageQuery(@RequestBody DynamicQuery query) {
        return service.dynamicQuery(query);
    }

    @PostMapping("query")
    @Operation(summary = "查询-不分页")
    public List<ModelFieldDTO> query(@RequestBody ModelFieldQuery query) {
        return service.query(query);
    }
}
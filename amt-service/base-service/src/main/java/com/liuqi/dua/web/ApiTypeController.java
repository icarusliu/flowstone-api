package com.liuqi.dua.web;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.liuqi.common.base.bean.query.DynamicQuery;
import com.liuqi.dua.bean.dto.ApiTypeDTO;
import com.liuqi.dua.bean.query.ApiTypeQuery;
import com.liuqi.dua.bean.req.ApiTypeAddReq;
import com.liuqi.dua.bean.req.ApiTypeUpdateReq;
import com.liuqi.dua.service.ApiTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 接口分类控制器
 *
 * @author Coder Generator 2024-08-09 22:08:31
 **/
@RestController
@RequestMapping("/base/api-type")
@Slf4j
@Tag(name = "控制器")
public class ApiTypeController {
    @Autowired
    private ApiTypeService service;

    @PostMapping("add")
    @Operation(summary = "新增")
    public void add(@RequestBody @Validated ApiTypeAddReq req) {
        ApiTypeDTO dto = new ApiTypeDTO();
        BeanUtils.copyProperties(req, dto);
        service.insert(dto);
       
    }

    @PutMapping("update")
    @Operation(summary = "更新")
    public void update(@RequestBody @Validated ApiTypeUpdateReq req) {
        ApiTypeDTO dto = new ApiTypeDTO();
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
    public ApiTypeDTO findById(@PathVariable("id") String id) {
        return service.findById(id).orElse(null);
    }

    @PostMapping("page-query")
    @Operation(summary = "查询-分页")
    public IPage<ApiTypeDTO> pageQuery(@RequestBody ApiTypeQuery query) {
        return service.pageQuery(query);
    }

    @PostMapping("filter")
    @Operation(summary = "查询-动态")
    public IPage<ApiTypeDTO> pageQuery(@RequestBody DynamicQuery query) {
        return service.dynamicQuery(query);
    }

    @PostMapping("query")
    @Operation(summary = "查询-不分页")
    public List<ApiTypeDTO> query(@RequestBody ApiTypeQuery query) {
        return service.query(query);
    }

    @GetMapping("table-tree")
    public List<ApiTypeDTO> tree() {
        return service.tree();
    }
}
package com.liuqi.base.web;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.liuqi.base.service.DeptService;
import com.liuqi.base.bean.dto.DeptDTO;
import com.liuqi.base.bean.query.DeptQuery;
import com.liuqi.base.bean.req.DeptAddReq;
import com.liuqi.base.bean.req.DeptUpdateReq;
import com.liuqi.common.base.bean.dto.Tree;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/base/dept")
@Slf4j
@Tag(name = "控制器")
public class DeptController {
    @Autowired
    private DeptService service;

    @PostMapping("add")
    @Operation(summary = "新增")
    public void add(@RequestBody @Validated DeptAddReq req) {
        DeptDTO dto = new DeptDTO();
        BeanUtils.copyProperties(req, dto);
        service.insert(dto);
       
    }

    @PutMapping("update")
    @Operation(summary = "更新")
    public void update(@RequestBody DeptUpdateReq req) {
        DeptDTO dto = new DeptDTO();
        BeanUtils.copyProperties(req, dto);
        service.update(dto);
       
    }

    @DeleteMapping("delete/{id}")
    @Operation(summary = "删除")
    public void delete(@PathVariable("id") String id) {
        service.delete(id);
       
    }

    @GetMapping("tree")
    @Operation(summary = "获取机构树")
    public List<Tree<DeptDTO>> getTree() {
        return service.getTree();
    }

    @PostMapping("page-query")
    @Operation(summary = "查询-分页")
    public IPage<DeptDTO> pageQuery(@RequestBody DeptQuery query) {
        return service.pageQuery(query);
    }

    @PostMapping("query")
    @Operation(summary = "查询-不分页")
    public List<DeptDTO> query(@RequestBody DeptQuery query) {
        return service.query(query);
    }
}
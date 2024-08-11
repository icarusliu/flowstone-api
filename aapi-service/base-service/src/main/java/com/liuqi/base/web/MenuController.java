package com.liuqi.base.web;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.liuqi.base.service.MenuService;
import com.liuqi.base.bean.dto.MenuDTO;
import com.liuqi.base.bean.query.MenuQuery;
import com.liuqi.base.bean.req.MenuAddReq;
import com.liuqi.base.bean.req.MenuUpdateReq;
import com.liuqi.common.base.bean.dto.Tree;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/base/menu")
@Slf4j
@Tag(name = "控制器")
public class MenuController {
    @Autowired
    private MenuService service;

    @PostMapping("add")
    @Operation(summary = "新增")
    public Mono<Void> add(@RequestBody @Validated MenuAddReq req) {
        MenuDTO dto = new MenuDTO();
        BeanUtils.copyProperties(req, dto);
        service.insert(dto);
        return Mono.empty();
    }

    @PutMapping("update")
    @Operation(summary = "更新")
    public Mono<Void> update(@RequestBody @Validated MenuUpdateReq req) {
        MenuDTO dto = new MenuDTO();
        BeanUtils.copyProperties(req, dto);
        service.update(dto);
        return Mono.empty();
    }

    @DeleteMapping("delete/{id}")
    @Operation(summary = "删除")
    public Mono<Void> delete(@PathVariable("id") String id) {
        service.delete(id);
        return Mono.empty();
    }

    @PostMapping("page-query")
    @Operation(summary = "查询-分页")
    public IPage<MenuDTO> pageQuery(@RequestBody MenuQuery query) {
        return service.pageQuery(query);
    }

    @PostMapping("query")
    @Operation(summary = "查询-不分页")
    public List<MenuDTO> query(@RequestBody MenuQuery query) {
        return service.query(query);
    }

    @GetMapping("tree")
    @Operation(summary = "获取树形结构")
    public List<Tree<MenuDTO>> getTree() {
        return service.getTree();
    }
}
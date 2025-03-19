package com.liuqi.sys.web;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.liuqi.sys.bean.dto.MenuDTO;
import com.liuqi.sys.bean.query.MenuQuery;
import com.liuqi.sys.bean.req.MenuAddReq;
import com.liuqi.sys.bean.req.MenuUpdateReq;
import com.liuqi.sys.service.MenuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/sys/menu")
@Slf4j
@Tag(name = "控制器")
public class MenuController {
    @Autowired
    private MenuService service;

    @PostMapping("add")
    @Operation(summary = "新增")
    public void add(@RequestBody @Validated MenuAddReq req) {
        MenuDTO dto = new MenuDTO();
        BeanUtils.copyProperties(req, dto);
        service.insert(dto);

    }

    @PutMapping("update")
    @Operation(summary = "更新")
    public void update(@RequestBody @Validated MenuUpdateReq req) {
        MenuDTO dto = new MenuDTO();
        BeanUtils.copyProperties(req, dto);
        service.update(dto);

    }

    @DeleteMapping("delete/{id}")
    @Operation(summary = "删除")
    public void delete(@PathVariable("id") String id) {
        service.delete(id);

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
    public List<MenuDTO> tree(
            @RequestParam(value = "appId", defaultValue = "sys") String appId,
            Boolean withHide,
            Boolean withButtons) {
        return service.getTree(appId, withHide, withButtons);
    }
}
package com.liuqi.base.web;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.liuqi.base.bean.dto.MenuButtonDTO;
import com.liuqi.base.bean.query.MenuButtonQuery;
import com.liuqi.base.bean.req.MenuButtonAddReq;
import com.liuqi.base.bean.req.MenuButtonUpdateReq;
import com.liuqi.base.bean.req.MenuButtonsSaveReq;
import com.liuqi.base.service.MenuButtonService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 菜单按钮控制器
 *
 * @author Coder Generator 2024-09-29 18:55:25
 **/
@RestController
@RequestMapping("/base/menu-button")
@Slf4j
@Tag(name = "控制器")
public class MenuButtonController {
    @Autowired
    private MenuButtonService service;

    @PostMapping("add")
    @Operation(summary = "新增")
    public void add(@RequestBody @Validated MenuButtonAddReq req) {
        MenuButtonDTO dto = new MenuButtonDTO();
        BeanUtils.copyProperties(req, dto);
        service.insert(dto);
    }

    @PutMapping("update")
    @Operation(summary = "更新")
    public void update(@RequestBody @Validated MenuButtonUpdateReq req) {
        MenuButtonDTO dto = new MenuButtonDTO();
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
    public MenuButtonDTO findById(@PathVariable("id") String id) {
        return service.findById(id).orElse(null);
    }

    @PostMapping("page-query")
    @Operation(summary = "查询-分页")
    public IPage<MenuButtonDTO> pageQuery(@RequestBody MenuButtonQuery query) {
        return service.pageQuery(query);
    }

    @PostMapping("query")
    @Operation(summary = "查询-不分页")
    public List<MenuButtonDTO> query(@RequestBody MenuButtonQuery query) {
        return service.query(query);
    }

    @PostMapping("save")
    @Operation(summary = "保存菜单按钮")
    public void save(@RequestBody @Validated MenuButtonsSaveReq req) {
        service.saveMenuButtons(req.getMenuId(), req.getButtons());
    }
}
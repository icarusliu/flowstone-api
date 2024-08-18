package com.liuqi.base.web;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.liuqi.base.bean.dto.DictItemDTO;
import com.liuqi.base.bean.query.DictItemQuery;
import com.liuqi.base.bean.req.DictItemAddReq;
import com.liuqi.base.bean.req.DictItemUpdateReq;
import com.liuqi.base.service.DictItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/base/dict-item")
@Slf4j
@Tag(name = "控制器")
public class DictItemController {
    @Autowired
    private DictItemService dictItemService;

    @PostMapping("add")
    @Operation(summary = "新增")
    public void add(@RequestBody @Validated DictItemAddReq req) {
        DictItemDTO dto = new DictItemDTO();
        BeanUtils.copyProperties(req, dto);
        dictItemService.insert(dto);
       
    }

    @PutMapping("update")
    @Operation(summary = "更新")
    public void update(@RequestBody DictItemUpdateReq req) {
        DictItemDTO dto = new DictItemDTO();
        BeanUtils.copyProperties(req, dto);
        dictItemService.update(dto);
       
    }

    @DeleteMapping("delete/{id}")
    @Operation(summary = "删除")
    public void delete(@PathVariable("id") String id) {
        dictItemService.delete(id);
       
    }

    @PostMapping("page-query")
    @Operation(summary = "查询-分页")
    public IPage<DictItemDTO> pageQuery(@RequestBody DictItemQuery query) {
        return dictItemService.pageQuery(query);
    }

    @PostMapping("query")
    @Operation(summary = "查询-不分页")
    public List<DictItemDTO> query(@RequestBody DictItemQuery query) {
        return dictItemService.query(query);
    }
}
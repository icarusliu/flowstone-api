package com.liuqi.acode.base.web;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.liuqi.acode.base.bean.dto.DictDTO;
import com.liuqi.acode.base.bean.query.DictQuery;
import com.liuqi.acode.base.bean.req.DictAddReq;
import com.liuqi.acode.base.bean.req.DictUpdateReq;
import com.liuqi.acode.base.service.DictService;
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
@RequestMapping("/base/dict")
@Slf4j
@Tag(name = "控制器")
public class DictController {
    @Autowired
    private DictService dictService;

    @PostMapping("add")
    @Operation(summary = "新增")
    public Mono<Void> add(@RequestBody @Validated DictAddReq req) {
        DictDTO dto = new DictDTO();
        BeanUtils.copyProperties(req, dto);
        dictService.insert(dto);
        return Mono.empty();
    }

    @PutMapping("update")
    @Operation(summary = "更新")
    public Mono<Void> update(@RequestBody DictUpdateReq req) {
        DictDTO dto = new DictDTO();
        BeanUtils.copyProperties(req, dto);
        dictService.update(dto);
        return Mono.empty();
    }

    @DeleteMapping("delete/{id}")
    @Operation(summary = "删除")
    public Mono<Void> delete(@PathVariable("id") String id) {
        dictService.delete(id);
        return Mono.empty();
    }

    @PostMapping("page-query")
    @Operation(summary = "查询-分页")
    public IPage<DictDTO> pageQuery(@RequestBody DictQuery query) {
        return dictService.pageQuery(query);
    }

    @PostMapping("query")
    @Operation(summary = "查询-不分页")
    public List<DictDTO> query(@RequestBody DictQuery query) {
        return dictService.query(query);
    }
}
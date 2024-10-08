package com.liuqi.base.web;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.liuqi.base.bean.dto.OperationDTO;
import com.liuqi.base.bean.query.OperationQuery;
import com.liuqi.base.bean.req.OperationAddReq;
import com.liuqi.base.bean.req.OperationUpdateReq;
import com.liuqi.base.service.OperationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/base/operation")
@Slf4j
@Tag(name = "控制器")
public class OperationController {
    @Autowired
    private OperationService service;

    @PostMapping("add")
    @Operation(summary = "新增")
    public void add(@RequestBody OperationAddReq req) {
        OperationDTO dto = new OperationDTO();
        BeanUtils.copyProperties(req, dto);
        service.insert(dto);
       
    }

    @PutMapping("update")
    @Operation(summary = "更新")
    public void update(@RequestBody OperationUpdateReq req) {
        OperationDTO dto = new OperationDTO();
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
    public IPage<OperationDTO> pageQuery(@RequestBody OperationQuery query) {
        return service.pageQuery(query);
    }

    @PostMapping("query")
    @Operation(summary = "查询-不分页")
    public List<OperationDTO> query(@RequestBody OperationQuery query) {
        return service.query(query);
    }
}
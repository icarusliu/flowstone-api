package com.liuqi.sys.web;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.liuqi.sys.bean.dto.SysConfigDTO;
import com.liuqi.sys.bean.query.SysConfigQuery;
import com.liuqi.sys.bean.req.SysConfigAddReq;
import com.liuqi.sys.bean.req.SysConfigUpdateReq;
import com.liuqi.sys.service.SysConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/base/config")
@Slf4j
@Tag(name = "控制器")
public class SysConfigController {
    @Autowired
    private SysConfigService sysConfigService;

    @PostMapping("add")
    @Operation(summary = "新增")
    public void add(@RequestBody @Validated SysConfigAddReq req) {
        SysConfigDTO dto = new SysConfigDTO();
        BeanUtils.copyProperties(req, dto);
        dto.setEnabled(true);
        sysConfigService.insert(dto);
       
    }

    @PutMapping("update")
    @Operation(summary = "更新")
    public void update(@RequestBody SysConfigUpdateReq req) {
        SysConfigDTO dto = new SysConfigDTO();
        BeanUtils.copyProperties(req, dto);
        sysConfigService.update(dto);
       
    }

    @DeleteMapping("delete/{id}")
    @Operation(summary = "删除")
    public void delete(@PathVariable("id") String id) {
        sysConfigService.delete(id);
    }

    @PostMapping("page-query")
    @Operation(summary = "查询-分页")
    public IPage<SysConfigDTO> pageQuery(@RequestBody SysConfigQuery query) {
        return sysConfigService.pageQuery(query);
    }


    @GetMapping("list")
    @Operation(summary = "查找所有配置项")
    public List<SysConfigDTO> getAll() {
        return sysConfigService.findAll();
    }

    @GetMapping("find-by-code/{code}")
    @Operation(summary = "根据编码查找配置项")
    public SysConfigDTO findByCode(@PathVariable("code") String code) {
        return sysConfigService.findByCode(code)
                .orElse(null);
    }
}
package com.liuqi.base.web;

import com.liuqi.base.service.SysConfigService;
import com.liuqi.base.bean.dto.SysConfigDTO;
import com.liuqi.base.bean.query.SysConfigQuery;
import com.liuqi.base.bean.req.SysConfigAddReq;
import com.liuqi.base.bean.req.SysConfigUpdateReq;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/base/sys-config")
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

    @GetMapping("list")
    @Operation(summary = "查找所有配置项")
    public List<SysConfigDTO> getAll() {
        return sysConfigService.query(SysConfigQuery.builder().build());
    }

    @GetMapping("find-by-code/{code}")
    @Operation(summary = "根据编码查找配置项")
    public SysConfigDTO findByCode(@PathVariable("code") String code) {
        return sysConfigService.findByCode(code)
                .orElse(null);
    }
}
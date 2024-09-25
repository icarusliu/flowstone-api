package com.liuqi.base.web;

import cn.hutool.core.lang.UUID;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.liuqi.base.bean.dto.ClientDTO;
import com.liuqi.base.bean.query.ClientQuery;
import com.liuqi.base.bean.req.ClientAddReq;
import com.liuqi.base.bean.req.ClientUpdateReq;
import com.liuqi.base.service.ClientService;
import com.liuqi.common.base.bean.query.DynamicQuery;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 客户端控制器 
 * @author Coder Generator 2024-09-25 09:04:45 
 **/
@RestController
@RequestMapping("/base/client")
@Slf4j
@Tag(name = "客户端控制器")
public class ClientController {
    @Autowired
    private ClientService service;

    @PostMapping("add")
    @Operation(summary = "新增")
    public void add(@RequestBody @Validated ClientAddReq req) {
        ClientDTO dto = new ClientDTO();
        BeanUtils.copyProperties(req, dto);
        dto.setDisabled(false);

        // 自动生成密钥
        dto.setSecret(UUID.randomUUID().toString().replace("-", ""));

        service.insert(dto);
    }

    @GetMapping("reset-secret")
    @Operation(summary = "重新生成密钥")
    public String resetSecret(@RequestParam("id") String id) {
        ClientDTO dto = new ClientDTO();
        dto.setId(id);
        dto.setSecret(UUID.randomUUID().toString().replace("-", ""));
        service.update(dto);
        return dto.getSecret();
    }

    @PutMapping("update")
    @Operation(summary = "更新")
    public void update(@RequestBody @Validated ClientUpdateReq req) {
        ClientDTO dto = new ClientDTO();
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
    public ClientDTO findById(@PathVariable("id") String id) {
        return service.findById(id).orElse(null);
    }

    @PostMapping("page-query")
    @Operation(summary = "查询-分页")
    public IPage<ClientDTO> pageQuery(@RequestBody ClientQuery query) {
        return service.pageQuery(query);
    }

    @PostMapping("filter")
    @Operation(summary = "查询-动态")
    public IPage<ClientDTO> pageQuery(@RequestBody DynamicQuery query) {
        return service.dynamicQuery(query);
    }

    @PostMapping("query")
    @Operation(summary = "查询-不分页")
    public List<ClientDTO> query(@RequestBody ClientQuery query) {
        return service.query(query);
    }
}
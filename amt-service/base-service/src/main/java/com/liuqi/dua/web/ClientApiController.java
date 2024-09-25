package com.liuqi.dua.web;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.liuqi.dua.bean.dto.ApiDTO;
import com.liuqi.dua.bean.dto.ClientApiDTO;
import com.liuqi.dua.bean.query.ApiQuery;
import com.liuqi.dua.bean.query.ClientApiQuery;
import com.liuqi.dua.bean.req.ClientApiAddReq;
import com.liuqi.dua.bean.req.ClientApiDeleteReq;
import com.liuqi.dua.service.ApiService;
import com.liuqi.dua.service.ClientApiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 客户端接口列表控制器 
 * @author Coder Generator 2024-09-25 15:58:19 
 **/
@RestController
@RequestMapping("/base/client-api")
@Slf4j
@Tag(name = "客户端接口列表控制器")
public class ClientApiController {
    @Autowired
    private ClientApiService service;

    @Autowired
    private ApiService apiService;

    @PostMapping("/find-by-client")
    @Operation(tags = "通过客户端id查询已授权接口列表")
    public IPage<ApiDTO> findByClient(@RequestBody ClientApiQuery query) {
        IPage<ClientApiDTO> apis = service.pageQuery(query);
        if (CollectionUtils.isEmpty(apis.getRecords())) {
            return new Page<>();
        }

        List<String> apiIds = apis.getRecords()
                .stream().map(ClientApiDTO::getApiId)
                .toList();

        List<ApiDTO> list = apiService.findByIds(apiIds);
        Page<ApiDTO> result = new Page<>();
        result.setTotal(apis.getTotal());
        result.setRecords(list);
        return result;
    }

    @PostMapping("find-not-bind")
    @Operation(tags = "查询未绑定的接口列表")
    public IPage<ApiDTO> findNotBind(@RequestBody ClientApiQuery query) {
        IPage<ClientApiDTO> apis = service.pageQuery(query);
        List<String> apiIds = apis.getRecords().stream().map(ClientApiDTO::getApiId)
                .toList();
        ApiQuery apiQuery = new ApiQuery();
        apiQuery.setIdsNot(apiIds);
        apiQuery.setPageNo(query.getPageNo());
        apiQuery.setPageSize(query.getPageSize());
        return apiService.pageQuery(apiQuery);
    }

    @PostMapping("/add")
    @Operation(tags = "增加客户端api授权")
    public void add(@RequestBody @Validated ClientApiAddReq req) {
        if (CollectionUtils.isEmpty(req.getApiIds())) {
            return;
        }

        List<ClientApiDTO> exists = service.findByClient(req.getClientId());
        List<String> apiIds = exists.stream().map(ClientApiDTO::getApiId)
                .toList();

        List<ClientApiDTO> list = req.getApiIds()
                .stream()
                .filter(apiId -> !apiIds.contains(apiId))
                .map(apiId -> {
                    ClientApiDTO dto = new ClientApiDTO();
                    dto.setApiId(apiId);
                    dto.setClientId(req.getClientId());
                    return dto;
                }).toList();
        service.insert(list);
    }

    @PostMapping("delete")
    @Operation(tags = "删除客户端api授权")
    public void delete(@RequestBody ClientApiDeleteReq req) {
        if (CollectionUtils.isEmpty(req.getApiIds())) {
            return;
        }
        service.delete(req.getClientId(), req.getApiIds());
    }
}
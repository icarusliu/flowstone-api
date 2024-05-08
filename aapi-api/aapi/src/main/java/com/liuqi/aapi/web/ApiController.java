package com.liuqi.aapi.web;

import com.liuqi.aapi.service.ApiCallService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 接口服务
 *
 * @author LiuQi 14:33
 **/
@RestController
@RequestMapping("/call")
public class ApiController {
    @Autowired
    private ApiCallService apiCallService;

    @RequestMapping("{path}")
    public Object execute(@PathVariable("path") String path,
                          @RequestHeader("tenantId") String tenantId,
                          @RequestParam Map<String, Object> params,
                          @RequestBody Object body,
                          @PathVariable Map<String, Object> pathVariables) {
        return apiCallService.call(tenantId, path, params, body, pathVariables);
    }
}

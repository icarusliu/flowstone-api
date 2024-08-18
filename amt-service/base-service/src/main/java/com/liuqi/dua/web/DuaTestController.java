package com.liuqi.dua.web;

import com.liuqi.dua.service.DuaService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("dua-test")
@Tag(name = "统一数据API测试控制器")
@Slf4j
public class DuaTestController {
    @Autowired
    private DuaService duaService;

    @Autowired
    private HttpServletRequest request;

    @RequestMapping(value = "**", method = {RequestMethod.GET, RequestMethod.DELETE})
    public Object execute(
            @RequestParam Map<String, Object> params
    ) {
        String key = request.getRequestURI().substring(10);
        return duaService.test(key, params);
    }

    @RequestMapping(value = "**", method = {RequestMethod.POST, RequestMethod.PUT})
    public Object executePost(
            @RequestBody(required = false) Map<String, Object> body) {
        String key = request.getRequestURI().substring(10);
        Map<String, Object> finalParams = new HashMap<>(16);

        if (null != body) {
            finalParams.putAll(body);
        }

        return duaService.test(key, finalParams);
    }
}

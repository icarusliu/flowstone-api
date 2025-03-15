package com.liuqi.dua.web;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.liuqi.common.base.bean.query.DynamicQuery;
import com.liuqi.dua.service.ModelDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 模型数据控制器
 *
 * @author  LiuQi 2025/3/14-21:31
 * @version V1.0
 **/
@RestController
@RequestMapping("/dua/model/data")
public class ModelDataController {
    @Autowired
    private ModelDataService modelDataService;

    @PostMapping("{modelId}/page-query")
    public IPage<Map<String, Object>> pageQuery(
            @PathVariable("modelId") String modelId,
            @RequestBody DynamicQuery query) {
        return modelDataService.pageQuery(modelId, query);
    }

    @PostMapping("{modelId}/save")
    public Map<String, Object> save(@PathVariable("modelId") String modelId,
                     @RequestBody Map<String, Object> body) {
        return modelDataService.save(modelId, body);
    }

    @DeleteMapping("{modelId}/delete")
    public void delete(@PathVariable("modelId") String modelId,
                       @RequestParam Map<String, Object> params) {
        modelDataService.delete(modelId, params);
    }
}

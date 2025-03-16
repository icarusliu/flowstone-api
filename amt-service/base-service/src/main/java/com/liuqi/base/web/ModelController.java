package com.liuqi.base.web;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.liuqi.sys.service.DictService;
import com.liuqi.common.ErrorCodes;
import com.liuqi.common.base.bean.query.DynamicQuery;
import com.liuqi.common.exception.AppException;
import com.liuqi.base.bean.dto.ModelDTO;
import com.liuqi.base.bean.dto.ModelDetailDTO;
import com.liuqi.base.bean.dto.ModelPublishedDTO;
import com.liuqi.base.bean.query.ModelQuery;
import com.liuqi.base.bean.req.ModelAddReq;
import com.liuqi.base.bean.req.ModelUpdateReq;
import com.liuqi.base.service.ModelPublishedService;
import com.liuqi.base.service.ModelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 模型控制器 
 * @author Coder Generator 2025-03-14 12:45:23 
 **/
@RestController
@RequestMapping("/dua/model")
@Slf4j
@Tag(name = "模型控制器")
public class ModelController {
    @Autowired
    private ModelService service;

    @Autowired
    private ModelPublishedService publishedService;

    @Autowired
    private DictService dictService;

    @PostMapping("add")
    @Operation(summary = "新增")
    public ModelDTO add(@RequestBody @Validated ModelAddReq req) {
        return service.insert(req);
    }

    @PutMapping("update")
    @Operation(summary = "更新")
    public void update(@RequestBody @Validated ModelUpdateReq req) {
        service.update(req);
    }

    @DeleteMapping("delete/{id}")
    @Operation(summary = "删除")
    public void delete(@PathVariable("id") String id) {
        service.delete(id);
    }

    @GetMapping("detail/{id}")
    @Operation(summary = "根据id查找记录")
    public ModelDetailDTO findById(@PathVariable("id") String id) {
       return service.getDetail(id);
    }

    @PostMapping("page-query")
    @Operation(summary = "查询-分页")
    public IPage<ModelDTO> pageQuery(@RequestBody ModelQuery query) {
        return service.pageQuery(query);
    }

    @PostMapping("filter")
    @Operation(summary = "查询-动态")
    public IPage<ModelDTO> pageQuery(@RequestBody DynamicQuery query) {
        return service.dynamicQuery(query);
    }

    @PostMapping("query")
    @Operation(summary = "查询-不分页")
    public List<ModelDTO> query(@RequestBody ModelQuery query) {
        return service.query(query);
    }

    @GetMapping("publish")
    public void publish(String id) {
        service.publish(id);
    }

    @GetMapping("offline")
    public void offline(String id) {
        service.offline(id);
    }

    @GetMapping("published/{id}")
    public ModelPublishedDTO getPublished(@PathVariable("id") String id) {
        ModelPublishedDTO publishedDTO = publishedService.findById(id).orElseThrow(AppException.supplier(ErrorCodes.DUA_MODEL_NOT_PUBLISHED));

        // 补充所使用的字典列表，需要取表单字段及列表字段等的字典
        List<String> dictCodes = new ArrayList<>(16);
        if (!CollectionUtils.isEmpty(publishedDTO.getFields())) {
            publishedDTO.getFields().forEach(field -> {
                String dictCode = field.getDictCode();
                if (StringUtils.isNotBlank(dictCode)) {
                    dictCodes.add(dictCode);
                }
            });
        }

        if (!CollectionUtils.isEmpty(publishedDTO.getListFields())) {
            publishedDTO.getListFields().forEach(m -> {
                String dictCode = MapUtils.getString(m, "dictCode");
                if (StringUtils.isNotBlank(dictCode)) {
                    dictCodes.add(dictCode);
                }
            });
        }

        if (!CollectionUtils.isEmpty(publishedDTO.getFormFields())) {
            publishedDTO.getFormFields().forEach(m -> {
                String dictCode = MapUtils.getString(m, "dictCode");
                if (StringUtils.isNotBlank(dictCode)) {
                    dictCodes.add(dictCode);
                }
            });
        }

        if (!CollectionUtils.isEmpty(dictCodes)) {
            publishedDTO.setDictList(dictService.findByCodes(dictCodes));
        }

        return publishedDTO;
    }
}
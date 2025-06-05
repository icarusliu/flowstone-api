package com.liuqi.dua.web;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.toolkit.SqlRunner;
import com.liuqi.common.utils.DynamicSqlHelper;
import com.liuqi.dua.bean.dto.ApiDTO;
import com.liuqi.dua.bean.dto.ApiDraftDTO;
import com.liuqi.dua.bean.dto.ApiHistoryDTO;
import com.liuqi.dua.bean.dto.ApiTypeDTO;
import com.liuqi.dua.bean.query.ApiHistoryQuery;
import com.liuqi.dua.bean.query.ApiQuery;
import com.liuqi.dua.bean.req.ApiAddReq;
import com.liuqi.dua.bean.req.ApiUpdateReq;
import com.liuqi.dua.service.ApiDraftService;
import com.liuqi.dua.service.ApiHistoryService;
import com.liuqi.dua.service.ApiService;
import com.liuqi.dua.service.ApiTypeService;
import com.liuqi.sys.bean.dto.DeptDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 接口控制器
 *
 * @author Coder Generator 2024-07-08 22:32:36
 **/
@RestController
@RequestMapping("/base/api")
@Slf4j
@Tag(name = "控制器")
public class ApiController {
    @Autowired
    private ApiService service;

    @Autowired
    private ApiTypeService typeService;

    @Autowired
    private ApiHistoryService historyService;

    @Autowired
    private ApiDraftService apiDraftService;

    @PostMapping("add")
    @Operation(summary = "新增")
    public void add(@RequestBody @Validated ApiAddReq req) {
        ApiDTO dto = new ApiDTO();
        BeanUtils.copyProperties(req, dto);
        service.insert(dto);
    }

    @PutMapping("update")
    @Operation(summary = "更新")
    public void update(@RequestBody @Validated ApiUpdateReq req) {
        ApiDTO dto = new ApiDTO();
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
    public ApiDTO findById(@PathVariable("id") String id) {
        return service.findById(id).orElse(null);
    }

    @PostMapping("page-query")
    @Operation(summary = "查询-分页")
    public IPage<ApiDTO> pageQuery(@RequestBody ApiQuery query) {
        IPage<ApiDTO> page = service.pageQuery(query);

        // 需要补充分类名称及使用次数
        if (page.getTotal() == 0) {
            return page;
        }

        List<String> apiIds = new ArrayList<>(16);
        List<String> typeIds = page.getRecords().stream()
                .peek(item -> apiIds.add(item.getId()))
                .map(ApiDTO::getTypeId)
                .toList();
        Map<String, String> typeMap = typeService.findByIds(typeIds)
                .stream()
                .collect(Collectors.toMap(ApiTypeDTO::getId, ApiTypeDTO::getName));
        Map<String, ApiDraftDTO> draftMap = apiDraftService.queryBuilder()
                .in("id", apiIds)
                .query(ApiDraftDTO::getId);

        page.getRecords().forEach(item -> {
            item.setTypeName(typeMap.get(item.getTypeId()));
            ApiDraftDTO draft = draftMap.get(item.getId());
            if (null != draft) {
                item.setSuccessCount(draft.getSuccessCount());
                item.setFailedCount(draft.getFailedCount());
            }
        });

        return page;
    }

    @PostMapping("query")
    @Operation(summary = "查询-不分页")
    public List<ApiDTO> query(@RequestBody ApiQuery query) {
        return service.query(query);
    }

    @GetMapping("history")
    public List<ApiHistoryDTO> queryHistory(String id) {
        ApiHistoryQuery query = new ApiHistoryQuery();
        query.setApiId(id);
        return historyService.query(query);
    }

    /**
     * 获取总调用次数
     */
    @GetMapping("total-called")
    public Long totalCalled() {
        List<Map<String, Object>> list = SqlRunner.db().selectList("select sum(failed_count) + sum(success_count) as c from d_api_draft");
        return MapUtils.getLongValue(list.get(0), "c");
    }
}
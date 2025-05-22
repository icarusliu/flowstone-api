package com.liuqi.common.base.bean.query;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
public class BaseQuery {
    @Schema(description = "id")
    private String id;

    @Schema(description = "id列表")
    private List<String> ids;

    @Schema(description = "每页记录数")
    private Long pageSize;

    @Schema(description = "页码，从1开始")
    private Long pageNo;

    @Schema(description = "排除的字段列表", hidden = true)
    private List<String> excludeFields;

    @Schema(description = "排序字段")
    private List<OrderBy> orderBys;
}

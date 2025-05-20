package com.liuqi.common.base.bean.query;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
public class BaseQuery {
    private String id;

    private List<String> ids;

    private Long pageSize;

    private Long pageNo;

    @Schema(description = "排除的字段列表")
    private List<String> excludeFields;
}

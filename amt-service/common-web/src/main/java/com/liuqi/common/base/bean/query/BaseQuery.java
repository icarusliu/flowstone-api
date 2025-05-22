package com.liuqi.common.base.bean.query;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 基础查询条件
 *
 * @author  LiuQi 2025/5/21-20:40
 * @version V1.0
 **/
@Data
public class BaseQuery {
    /**
     * id
     */
    private String id;

    /**
     * id列表
     */
    private List<String> ids;

    /**
     * 每页记录数
     */
    private Long pageSize;

    /**
     * 页码，从1开始
     */
    private Long pageNo;

    @Schema(description = "排除的字段列表")
    private List<String> excludeFields;
}

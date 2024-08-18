package com.liuqi.dua.bean.query;

import com.liuqi.common.base.bean.query.BaseQuery;
import lombok.Data;

import java.util.List;

/**
 * 接口草稿查询对象
 *
 * @author Coder Generator 2024-07-08 22:33:46
 **/
@Data
public class ApiDraftQuery extends BaseQuery {
    private String path;
    private String key;
    private String keyLike;
    private List<String> typeIds;
}
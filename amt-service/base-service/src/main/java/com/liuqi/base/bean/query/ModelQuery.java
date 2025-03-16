package com.liuqi.base.bean.query;

import com.liuqi.common.base.bean.query.BaseQuery;
import lombok.Data;

/**
 * 模型查询对象 
 * @author Coder Generator 2025-03-14 12:45:23 
 **/
@Data
public class ModelQuery extends BaseQuery {
    private String typeId;
    private String code;
    private String name;
}
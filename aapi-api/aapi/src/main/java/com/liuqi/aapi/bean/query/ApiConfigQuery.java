package com.liuqi.aapi.bean.query;

import com.liuqi.common.base.bean.query.BaseQuery;
import lombok.Data;

/** API配置表查询对象 
 * @author Coder Generator 2024-04-18 14:25:01 **/
@Data
public class ApiConfigQuery extends BaseQuery {
    private String path;
    private String tenantId;
}
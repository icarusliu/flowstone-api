package com.liuqi.dua.bean.query;

import com.liuqi.common.base.bean.query.BaseQuery;
import lombok.Data;

/**
 * 客户端接口列表查询对象 
 * @author Coder Generator 2024-09-25 15:58:18 
 **/
@Data
public class ClientApiQuery extends BaseQuery {
    private String clientId;
    private String apiId;
}
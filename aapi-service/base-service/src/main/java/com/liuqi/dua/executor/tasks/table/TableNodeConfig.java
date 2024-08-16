package com.liuqi.dua.executor.tasks.table;

import com.liuqi.dua.executor.bean.NodeParam;
import lombok.Data;

import java.util.List;

/**
 * 表节点数据配置
 *
 * @author  LiuQi 2024/8/9-10:54
 * @version V1.0
 **/
@Data
public class TableNodeConfig {
    private String ds;
    private String table;

    // 过滤条件，字段配置，配置每个字段是模糊还是精确等
    private List<NodeParam> params;

    // 是否分页
    private Boolean pageable;

    // 分页页码参数
    private NodeParam pageNo;

    // 分页记录数参数
    private NodeParam PageSize;
}

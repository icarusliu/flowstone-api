package com.liuqi.dua.executor.tasks.sql;

import com.liuqi.dua.executor.bean.NodeParam;
import com.liuqi.dua.executor.tasks.NodeConfig;
import lombok.Data;

import java.util.List;

/**
 * SQL节点配置
 *
 * @author  LiuQi 2024/8/9-10:16
 * @version V1.0
 **/
@Data
public class SqlNodeConfig extends NodeConfig {
    private String ds;
    private String sql;

    private List<NodeParam> params;
}

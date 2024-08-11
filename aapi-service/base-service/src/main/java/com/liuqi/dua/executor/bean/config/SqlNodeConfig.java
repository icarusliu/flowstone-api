package com.liuqi.dua.executor.bean.config;

import lombok.Data;

/**
 * SQL节点配置
 *
 * @author  LiuQi 2024/8/9-10:16
 * @version V1.0
 **/
@Data
public class SqlNodeConfig {
    private String ds;
    private String sql;
}

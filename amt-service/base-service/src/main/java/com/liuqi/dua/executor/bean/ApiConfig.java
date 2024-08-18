package com.liuqi.dua.executor.bean;

import lombok.Data;

import java.util.List;

/**
 * 接口配置
 *
 * @author  LiuQi 2024/8/8-22:21
 * @version V1.0
 **/
@Data
public class ApiConfig {
    // 节点信息
    List<NodeInfo> nodes;
}

package com.liuqi.dua.executor.bean;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 接口节点配置
 *
 * @author  LiuQi 2024/8/8-22:22
 * @version V1.0
 **/
@Data
public class NodeInfo {
    // 节点id
    private String id;

    // 节点编码
    private String code;

    // 节点名称
    private String name;

    // 节点备注
    private String remark;

    // 节点类型
    private String type;

    // 节点位置
    private String pos;

    // 父级节点列表
    private List<String> parentIds;

    // 节点附加配置
    private Map<String, Object> config;
}

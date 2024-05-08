package com.liuqi.aapi.bean.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * API配置
 *
 * @author LiuQi 15:38
 **/
@Data
public class ApiNode {
    private String code;

    private String name;

    private String remark;

    private String type;

    // 更多参数，根据节点类型不同有不同参数
    private Map<String, Object> config;

    // 节点参数配置
    private List<ParamConfig> paramConfigs;

    // 节点输入参数，根据实际运行情况生成的实际参数
    private Map<String, Object> params;
}

package com.liuqi.dua.executor.bean;

import lombok.Data;

/**
 * 任务输入
 *
 * @author  LiuQi 2024/8/8-22:08
 * @version V1.0
 **/
@Data
public class NodeInput {
    /**
     * 任务配置信息
     */
    private NodeInfo nodeInfo;

    /**
     * 运行上下文
     */
    private ApiExecutorContext context;
}

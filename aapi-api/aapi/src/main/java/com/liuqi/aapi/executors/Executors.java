package com.liuqi.aapi.executors;

import com.liuqi.aapi.bean.dto.ApiContext;
import com.liuqi.aapi.bean.dto.ApiNode;
import com.liuqi.aapi.bean.dto.ApiParams;
import com.liuqi.aapi.bean.dto.ParamConfig;
import com.liuqi.aapi.executors.nodes.SqlNodeExecutor;
import org.apache.commons.collections4.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 执行器集合
 *
 * @author LiuQi 16:17
 **/
public class Executors {
    private static Map<String, NodeExecutor> executors = new HashMap<>() {
        {
            put("sql", new SqlNodeExecutor());
        }
    };

    public static void execute(ApiContext context, ApiNode node) {
        String type = node.getType();
        NodeExecutor executor = executors.get(type);
        if (null == executor) {
            throw new RuntimeException("节点类型不存在");
        }

        // 设置节点参数
        List<ParamConfig> paramConfigs = node.getParamConfigs();
        Map<String, Object> params = new HashMap<>(context.getDefParams());
        Object nodeParams = Resolvers.resolve(context, paramConfigs);
        if (nodeParams instanceof Map<?, ?>) {
            params.putAll((Map<? extends String, ?>) nodeParams);
        }
        node.setParams(params);

        Object result = executor.execute(context, node);
        context.getParams().addNode(node.getCode(), params, result);
    }
}

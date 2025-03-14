package com.liuqi.etl.service.executors.job;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.dynamic.datasource.toolkit.DynamicDataSourceContextHolder;
import com.liuqi.common.utils.DynamicSqlHelper;
import com.liuqi.etl.bean.dto.EtlJobPublishedDTO;
import com.liuqi.etl.service.executors.EtlParamUtils;
import com.liuqi.etl.service.executors.config.EtlNodeInfo;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * SQL数据加工任务
 *
 * @author LiuQi 2025/3/14-8:10
 * @version V1.0
 **/
@Service
public class SqlProcessJob {
    /**
     * 执行SQL任务
     * @param job 任务定义
     * @param dataDate 数据日期
     */
    public void runSqlJob(EtlJobPublishedDTO job, LocalDate dataDate) {
        Map<String, Object> config = job.getConfig();
        JSONObject obj = JSON.parseObject(JSON.toJSONString(config));
        List<EtlNodeInfo> nodes = obj.getList("nodes", EtlNodeInfo.class);
        if (CollectionUtils.isEmpty(nodes)) {
            return;
        }

        // 目前只处理一个节点的情况，多节点使用DAG的情况后续扩展
        Map<String, Object> params = EtlParamUtils.getEtlParams(dataDate);
        EtlNodeInfo node = nodes.get(0);
        runSqlNode(node, params);
    }

    /**
     * 执行SQL节点
     * @param node 节点配置
     * @param params 运行参数
     */
    private void runSqlNode(EtlNodeInfo node, Map<String, Object> params) {
        Map<String, Object> config = node.getConfig();
        String ds = MapUtils.getString(config, "ds");
        String sql = MapUtils.getString(config, "sql");
        if (StringUtils.isNotBlank(ds)) {
            DynamicDataSourceContextHolder.push(ds);
        }

        String key = node.getId();
        if (StringUtils.isBlank(key)) {
            key = UUID.randomUUID().toString().replace("-", "");
        }

        try {
            DynamicSqlHelper.executeSql(key, sql, params);
        } finally {
            if (StringUtils.isNotBlank(ds)) {
                DynamicDataSourceContextHolder.poll();
            }
        }
    }
}

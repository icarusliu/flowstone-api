package com.liuqi.dua.executor.tasks.sql;

import com.alibaba.fastjson2.JSON;
import com.baomidou.dynamic.datasource.toolkit.DynamicDataSourceContextHolder;
import com.liuqi.common.utils.DynamicSqlHelper;
import com.liuqi.dua.executor.bean.ApiExecutorContext;
import com.liuqi.dua.executor.bean.NodeInfo;
import com.liuqi.dua.executor.bean.NodeInput;
import com.liuqi.dua.executor.AbstractDagTask;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.Map;

/**
 * DAG SQL任务
 * 用于执行对应的ETL任务
 *
 * @author LiuQi 2024/8/6-12:35
 * @version V1.0
 **/
@Slf4j
public class SqlTask extends AbstractDagTask<SqlNodeConfig> {
    public SqlTask(ApiExecutorContext apiExecutorContext, NodeInput input) {
        super(apiExecutorContext, input);
    }

    @Override
    public Object executeInternal() {
        NodeInput task = this.getId();
        return this.executeInternal(task, getNodeExecuteParams());
    }

    /**
     * 执行单个任务最终实现
     *
     * @param task   任务信息
     * @param params 执行参数
     * @return 执行结果
     */
    private Object executeInternal(NodeInput task, Map<String, Object> params) {
        NodeInfo nodeInfo = task.getNodeInfo();
        String code = nodeInfo.getCode();

        long startTime = System.currentTimeMillis();
        log.info("【{}】开始执行", nodeInfo.getCode());

        String ds = this.nodeConfig.getDs();

        // 切换动态数据源
        DynamicDataSourceContextHolder.push(ds);

        // 执行SQL语句，按;进行分隔分别执行，需要防止出现 &lt;&gt;&quot;&apos;&amp;这种情况；
        String configSql = this.nodeConfig.getSql();
        String[] sqls = configSql.split("(?<!&lt|&gt|&quot|&apos|&amp);");

        // 结果只取最后一条语句的
        Object resp = null;
        try {
            for (String sql : sqls) {
                if (StringUtils.isBlank(sql)) {
                    continue;
                }

                String key = "dagSqlTask-" + task.getNodeInfo().getId();
                resp = DynamicSqlHelper.executeSql(key, sql, params);
            }

              // 一次性执行时，不会返回最后一次执行的结果，因此分批执行
//            String key = "dagSqlTask-" + task.getId();
//            resp = DynamicSqlHelper.executeSql(key, config, params);
        } finally {
            // 数据源重置
            DynamicDataSourceContextHolder.poll();
        }

        if (log.isDebugEnabled()) {
            if (resp instanceof Collection<?> collection) {
                log.debug("【{}】执行结果：(数据量){}", code, collection.size());
            } else {
                log.debug("【{}】执行结果：{}", code, JSON.toJSONString(resp));
            }
            log.debug("【{}】执行耗时：{}ms", code, System.currentTimeMillis() - startTime);
        }

        return resp;
    }
}

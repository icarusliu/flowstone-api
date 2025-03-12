package com.liuqi.etl.service.executors;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.dynamic.datasource.toolkit.DynamicDataSourceContextHolder;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.liuqi.base.service.SysConfigService;
import com.liuqi.common.utils.DynamicSqlHelper;
import com.liuqi.etl.bean.dto.EtlDataSyncConfig;
import com.liuqi.etl.bean.dto.EtlJobPublishedDTO;
import com.liuqi.etl.service.EtlJobService;
import com.liuqi.etl.service.EtlLogService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;

/**
 * ETL数据同步服务
 *
 * @author LiuQi 2024/12/5-9:21
 * @version V1.0
 **/
@Service
@Slf4j
public class EtlDataSyncService {

    @Autowired
    private EtlJobService dataSyncService;

    @Autowired
    private EtlLogService etlLogService;

    @Autowired
    private SysConfigService sysConfigService;

    private final ExecutorService executorService = Executors.newFixedThreadPool(2);

    /**
     * 执行数据同步
     * @param job 任务配置
     */
    public void syncData(EtlJobPublishedDTO job, LocalDate dataDate) {
        Map<String, Object> configMap = job.getConfig();
        EtlDataSyncConfig config = JSONObject.parseObject(JSON.toJSONString(configMap), EtlDataSyncConfig.class);
        Map<String, Object> params = EtlParamUtils.getEtlParams(dataDate);
        this.syncInternal(job, config, params);
    }



    /**
     * 执行单个任务的数据同步
     *
     * @param config 数据同步对象
     * @param params      数据同步参数
     */
    private void syncInternal(EtlJobPublishedDTO job, EtlDataSyncConfig config, Map<String, Object> params) {
        // 先查找记录数
        long total = getTotal(job, config, params);

        if (total == 0) {
            log.debug("查询数据为空，无需同步");
            return;
        }

        // 执行前置处理
        String sourceDs = config.getSourceDs();
        String prepareSql = config.getPrepareSql();
        String jobId = job.getId();
        if (StringUtils.isNotBlank(prepareSql)) {
            callWithDs(config.getDestDs(), () -> DynamicSqlHelper.executeSql(jobId + "-prepare", prepareSql, params));
        }

        long startTime = System.currentTimeMillis();
        if (total <= config.getBatchSize()) {
            // 一次性同步
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> sourceDataList = callWithDs(sourceDs, () -> (List<Map<String, Object>>) DynamicSqlHelper.executeSql(jobId + "-sourceQuery",
                    config.getSourceSql(), params));

            if (CollectionUtils.isEmpty(sourceDataList)) {
                log.debug("查询数据为空，无需同步");
                return;
            }

            callWithDs(config.getDestDs(), () -> DynamicSqlHelper.batchInsert(jobId + "-destInsert", config.getDestSql(), sourceDataList));

            log.debug("{}同步完毕， 同步记录数：{}, 耗时：{}s", job.getName(), total, (System.currentTimeMillis() - startTime) / 1000);
        } else {
            // 需要分批次同步
            int batchSize = config.getBatchSize();
            int processed = 0;
            int page = 1;
            Map<String, Object> queryParams = new HashMap<>(params);
            String sourceSql = config.getSourceSql();
            sourceSql += " limit #{start},#{limit}";
            while (processed < total) {
                queryParams.put("start", (page - 1) * batchSize);
                queryParams.put("limit", batchSize);
                page++;

                long subStartTime = System.currentTimeMillis();
                String pSourceSql = sourceSql;
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> subList = callWithDs(config.getSourceDs(),
                        () -> (List<Map<String, Object>>) DynamicSqlHelper.executeSql(jobId + "-subquery", pSourceSql, queryParams));

                log.debug("查询数据耗时：{}ms", System.currentTimeMillis() - subStartTime);
                subStartTime = System.currentTimeMillis();

                if (CollectionUtils.isEmpty(subList)) {
                    break;
                }

               callWithDs(config.getDestDs(), () -> DynamicSqlHelper.batchInsert(jobId + "-insert", config.getDestSql(), subList));

                log.debug("数据批量写入耗时：{}ms", System.currentTimeMillis() - subStartTime);
                processed += batchSize;
            }

            log.debug("{}同步完成，同步记录数：{}，消耗时间： {}s", job.getName(), total, (System.currentTimeMillis() - startTime) / 1000);
        }
    }

    /**
     * 获取需要同步的总记录数
     */
    private static long getTotal(EtlJobPublishedDTO job, EtlDataSyncConfig config, Map<String, Object> params) {
        Map<String, Object> pageParams = new HashMap<>(params);
        pageParams.put("pageNo", 1);
        pageParams.put("pageSize", 1);

        return callWithDs(config.getSourceDs(), () -> {
            @SuppressWarnings("unchecked")
            IPage<Map<String, Object>> page = (IPage<Map<String, Object>>) DynamicSqlHelper.executeSql(job.getId() + "-sourceQuery",
                    config.getSourceSql(), pageParams);
            if (null == page) {
                return 0L;
            }
            return page.getTotal();
        });
    }

    private static <T> T callWithDs(String ds, Supplier<T> supplier) {
        DynamicDataSourceContextHolder.push(ds);
        try {
            return supplier.get();
        } finally {
            DynamicDataSourceContextHolder.poll();
        }
    }

    private static void callWithDs(String ds, Runnable task) {
        DynamicDataSourceContextHolder.push(ds);
        try {
            task.run();
        } finally {
            DynamicDataSourceContextHolder.poll();
        }
    }
}

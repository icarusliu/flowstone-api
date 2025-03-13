package com.liuqi.etl.service.executors;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.dynamic.datasource.toolkit.DynamicDataSourceContextHolder;
import com.liuqi.common.ErrorCodes;
import com.liuqi.common.exception.AppException;
import com.liuqi.common.utils.DynamicSqlHelper;
import com.liuqi.etl.bean.dto.EtlJobPublishedDTO;
import com.liuqi.etl.bean.dto.EtlJobStateDTO;
import com.liuqi.etl.bean.dto.EtlLogDTO;
import com.liuqi.etl.service.EtlJobPublishedService;
import com.liuqi.etl.service.EtlJobService;
import com.liuqi.etl.service.EtlJobStateService;
import com.liuqi.etl.service.EtlLogService;
import com.liuqi.etl.service.executors.config.EtlNodeInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * ETL服务
 *
 * @author LiuQi 2024/12/5-9:21
 * @version V1.0
 **/
@Service
@Slf4j
public class EtlService {
    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Autowired
    private EtlJobPublishedService etlJobPublishedService;

    @Autowired
    private EtlJobService etlJobService;

    @Autowired
    private EtlDataSyncService etlDataSyncService;

    @Autowired
    private EtlJobStateService etlJobStateService;

    @Autowired
    @Lazy
    private EtlDependCheckService etlDependCheckService;

    @Autowired
    @Lazy
    private EtlInitService initService;

    @Autowired
    private EtlLogService etlLogService;

    private static final ExecutorService executorService = Executors.newFixedThreadPool(10);

    /**
     * 执行指定任务
     * 不执行其后代任务
     * @param jobId 任务id
     * @param dataDate 数据日期
     */
    public void runJob(String jobId, LocalDate dataDate) {
        EtlJobPublishedDTO job = etlJobPublishedService.findById(jobId)
                .orElseThrow(() -> AppException.of(ErrorCodes.ETL_JOB_NOT_EXISTS));
        this.submit(job, dataDate, false);
    }

    /**
     * 执行指定任务
     * 不执行其后代任务
     * @param job 任务
     * @param dataDate 数据日期
     */
    public void runJob(EtlJobPublishedDTO job, LocalDate dataDate) {
        this.submit(job, dataDate, false);
    }

    /**
     * 执行任务及其子任务
     * @param jobId 任务id
     * @param dataDate 数据日期
     */
    public void runJobAndSub(String jobId, LocalDate dataDate) {
        EtlJobPublishedDTO job = etlJobPublishedService.findById(jobId)
                .orElseThrow(() -> AppException.of(ErrorCodes.ETL_JOB_NOT_EXISTS));
        this.submit(job, dataDate, true);
    }

    /**
     * 执行任务及其子任务
     * @param job 任务
     * @param dataDate 数据日期
     */
    public void runJobAndSub(EtlJobPublishedDTO job, LocalDate dataDate) {
        this.submit(job, dataDate, true);
    }

    /**
     * 测试任务
     * @param jobId 任务id
     * @param dataDate 数据日期
     */
    public void testJob(String jobId, LocalDate dataDate) {
        EtlJobPublishedDTO job = etlJobService.findById(jobId)
                .orElseThrow(() -> AppException.of(ErrorCodes.ETL_JOB_NOT_EXISTS));
        this.runInternal(job, dataDate, false, null);
    }

    /**
     * 执行指定任务及其所有后代任务
     * @param job 任务
     * @param dataDate 数据日期
     * @param withSubJobs 是否执行其后代任务
     */
    private void submit(EtlJobPublishedDTO job, LocalDate dataDate, Boolean withSubJobs) {
        // 更新任务状态
        EtlJobStateDTO stateDTO = new EtlJobStateDTO();
        stateDTO.setJobId(job.getId());
        stateDTO.setExecuteDate(dataDate);
        stateDTO.setState(0);
        stateDTO.setRunSub(withSubJobs);
        etlJobStateService.insert(stateDTO);

        if (!initService.initCompleted()) {
            // 启动过程中的任务不直接执行，而是进入队列等待初始化完成后再执行
            initService.addJob(job, dataDate, withSubJobs);
            return;
        }

        executorService.submit(() -> {
            EtlLogDTO logDTO = new EtlLogDTO();
            logDTO.setJobId(job.getId());
            logDTO.setJobCode(job.getCode());
            logDTO.setJobName(job.getName());
            logDTO.setDataDate(dataDate);
            logDTO.setExecuteTime(LocalDateTime.now());

            try {
                long startTime = System.currentTimeMillis();
                stateDTO.setState(1);
                etlJobStateService.update(stateDTO);
                runInternal(job, dataDate, withSubJobs, stateDTO);
                long spendTime = (System.currentTimeMillis() - startTime) / 1000;
                logDTO.setSpentTime(Math.toIntExact(spendTime));
                logDTO.setStatus(0);
                etlLogService.insert(logDTO);
            } catch (Exception ex) {
                log.error("任务执行异常", ex);
                stateDTO.setState(3);
                etlJobStateService.update(stateDTO);

                logDTO.setStatus(1);
                logDTO.setErrorMsg(ex.getMessage());
                etlLogService.insert(logDTO);
            }
        });
    }

    private void runInternal(EtlJobPublishedDTO job, LocalDate dataDate, Boolean withSubJobs, EtlJobStateDTO state) {
        // 先执行任务本身，然后记录任务执行状态，如果需要跑依赖任务，则触发其依赖任务跑批检查
        String type = job.getType();
        if ("sync".equals(type)) {
            // 数据同步任务
            etlDataSyncService.syncData(job, dataDate);
        } else {
            // 执行SQL任务
            this.runSqlJob(job, dataDate);
        }

        // 更新任务状态
        if (null != state) {
            state.setState(2);
            etlJobStateService.update(state);
        }

        if (withSubJobs) {
            // 触发依赖任务跑批检查
            etlDependCheckService.check(job.getId(), dataDate);
        }
    }

    /**
     * 执行SQL任务
     * @param job 任务定义
     * @param dataDate 数据日期
     */
    private void runSqlJob(EtlJobPublishedDTO job, LocalDate dataDate) {
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
        }finally {
            if (StringUtils.isNotBlank(ds)) {
                DynamicDataSourceContextHolder.poll();
            }
        }
    }
}

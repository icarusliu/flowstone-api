package com.liuqi.etl.service.executors;

import com.liuqi.etl.bean.dto.EtlJobPublishedDTO;
import com.liuqi.etl.bean.query.EtlJobPublishedQuery;
import com.liuqi.etl.service.EtlJobPublishedService;
import com.liuqi.sys.service.ScheduleTaskExecutorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.CronTask;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

/**
 * ETL定时任务调度器
 *
 * @author  LiuQi 2025/3/12-7:47
 * @version V1.0
 **/
@Service
@Slf4j
public class EtlJobScheduler implements ScheduleTaskExecutorService {
    @Autowired
    private ThreadPoolTaskScheduler threadPoolTaskScheduler;

    @Autowired
    private EtlJobPublishedService publishedService;

    @Autowired
    private EtlService etlService;

    private final Map<String, ScheduledFuture<?>> scheduledFutureMap = new ConcurrentHashMap<>(16);

    @Override
    public void startAll() {
        // 系统启动时启动所有不自动执行的作业（自动执行的由其依赖任务启动）
        EtlJobPublishedQuery query = new EtlJobPublishedQuery();
        query.setAutoTrigger(false);
        query.setCronNotNull(true);
        publishedService.query(query).forEach(this::startJob);
    }

    /**
     * 启动作业
     * @param job 作业
     */
    public void startJob(EtlJobPublishedDTO job) {
        CronTask cronTask = new CronTask(createRunnable(job), job.getCron());
        ScheduledFuture<?> future = threadPoolTaskScheduler.schedule(cronTask.getRunnable(), cronTask.getTrigger());
        scheduledFutureMap.put(job.getId(), future);
        log.info("任务启动成功");
    }

    private Runnable createRunnable(EtlJobPublishedDTO job) {
        return () -> etlService.runJobAndSub(job, LocalDate.now().minusDays(1));
    }

    /**
     * 停止作业
     * @param id 作业id
     */
    public void stopJob(String id) {
        Optional.ofNullable(scheduledFutureMap.get(id)).ifPresent(future -> future.cancel(true));
        scheduledFutureMap.remove(id);
        log.info("任务停止成功");
    }

    /**
     * 重启作业
     */
    public void restartJob(String jobId) {
        this.stopJob(jobId);
        publishedService.findById(jobId).ifPresent(this::startJob);
    }
}

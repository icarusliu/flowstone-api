package com.liuqi.base.task;

import com.liuqi.dua.service.ApiLogService;
import com.liuqi.etl.service.EtlLogService;
import com.liuqi.sys.task.NodePingTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * 日志清理
 *
 * @author  LiuQi 2025/3/16-19:44
 * @version V1.0
 **/
@Component
@Slf4j
public class LogClearTask {
    @Autowired
    private EtlLogService etlLogService;

    @Autowired
    private ApiLogService apiLogService;

    @Value("${app.logs.api.keep-days:60}")
    private Integer apiLogKeepDays;

    @Value("${app.logs.etl.keep-days:60}")
    private Integer etlLogKeepDays;

    @Scheduled(cron = "0 0 1 * * ?")
    public void clearLogs() {
        if (!NodePingTask.isMaster) {
            // 非主节点不执行
            return;
        }

        try {
            apiLogService.clearLogsBefore(LocalDate.now().minusDays(apiLogKeepDays));
        } catch (Exception ex) {
            log.error("日志清理失败", ex);
        }

        try {
            etlLogService.clearLogsBefore(LocalDate.now().minusDays(etlLogKeepDays));
        } catch (Exception ex) {
            log.error("日志清理失败", ex);
        }
    }
}

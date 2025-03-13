package com.liuqi.etl.service.executors;

import com.liuqi.common.utils.value.TripleValue;
import com.liuqi.etl.bean.dto.EtlJobDTO;
import com.liuqi.etl.bean.dto.EtlJobPublishedDTO;
import com.liuqi.etl.bean.dto.EtlJobStateDTO;
import com.liuqi.etl.bean.query.EtlJobQuery;
import com.liuqi.etl.bean.query.EtlJobStateQuery;
import com.liuqi.etl.service.EtlJobDependService;
import com.liuqi.etl.service.EtlJobService;
import com.liuqi.etl.service.EtlJobStateService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * ETl系统初始化服务
 *
 * @author LiuQi 2025/3/10-21:38
 * @version V1.0
 **/
@Service
@Slf4j
public class EtlInitService {
    @Autowired
    private EtlJobDependService etlJobDependService;

    @Autowired
    private EtlJobStateService jobStateService;

    @Autowired
    @Lazy
    private EtlService etlService;

    @Autowired
    private EtlJobService jobService;

    @Autowired
    private EtlDependCheckService checkService;

    /**
     * 系统状态，0：启动未自检完成，此时不能执行任务；1：启动已自检完成，此时可以执行任务
     */
    public static volatile int status = 0;

    private static final LinkedBlockingQueue<TripleValue<EtlJobPublishedDTO, LocalDate, Boolean>> INIT_LIST = new LinkedBlockingQueue<>();

    /**
     * 系统初始化是否完成
     * @return 是否完成
     */
    public boolean initCompleted() {
        return status == 1;
    }

    /**
     * 系统启动过程中，将提交执行的任务先提交到此，避免提交的任务与启动时的任务重复执行
     * @param job 任务
     * @param dataDate 数据日期
     * @param withSubJobs 是否执行子任务
     */
    public void addJob(EtlJobPublishedDTO job, LocalDate dataDate, Boolean withSubJobs) {
        TripleValue<EtlJobPublishedDTO, LocalDate, Boolean> value = new TripleValue<>();
        value.setFirstValue(job);
        value.setSecondValue(dataDate);
        value.setThirdValue(withSubJobs);
        try {
            INIT_LIST.put(value);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @PostConstruct
    public void initCheck() {
        log.info("系统初始化检查开始");
        // 系统启动时，需要检查哪些任务未完成，重新启动这些任务
        // 但这个里面有个逻辑比较麻烦，就是哪些任务是当天需要执行的；
        // 暂定所有任务都是每天都需要执行 TODO
        // 检查有两种情况，一种是部分任务已经在执行了，这个在state表中有记录；一种是任务还没执行，但其父任务执行了，正要检查子任务时系统重启，此时需要触发所有自动任务进行检查

        // 先检查state表中运行中或已提交但未运行的记录
        LocalDate dataDate = LocalDate.now().minusDays(1);
        EtlJobStateQuery query = new EtlJobStateQuery();
        query.setStates(Arrays.asList(0, 1));
        query.setExecuteDate(dataDate);
        List<EtlJobStateDTO> jobStateList = jobStateService.query(query);

        // 检查所有自动执行的子任务，看其条件是否达成，如果达成则触发其跑批操作
        EtlJobQuery jobQuery = new EtlJobQuery();
        jobQuery.setAutoTrigger(true);
        List<String> list = jobService.query(jobQuery).stream().map(EtlJobDTO::getId).toList();
        List<EtlJobPublishedDTO> toExecuteJobs = checkService.check(list, dataDate, null);

        // 查询完成后，修改状态，让定时任务直接提交到队列开始执行
        status = 1;

        // 触发提交后未执行但系统重启的任务
        jobStateList.forEach(item -> {
            String jobId = item.getJobId();
            if (item.getRunSub()) {
                etlService.runJobAndSub(jobId, dataDate);
            } else {
                etlService.runJob(jobId, dataDate);
            }
        });

        // 触发自动执行的任务执行
        toExecuteJobs.forEach(job -> {
            etlService.runJobAndSub(job, dataDate);
        });

        // 触发在系统启动过程中添加的任务
        INIT_LIST.forEach(item -> {
            if (item.getThirdValue()) {
                etlService.runJobAndSub(item.getFirstValue(), item.getSecondValue());
            } else {
                etlService.runJob(item.getFirstValue(), item.getSecondValue());
            }
        });

        log.info("系统初始化完成");
    }
}

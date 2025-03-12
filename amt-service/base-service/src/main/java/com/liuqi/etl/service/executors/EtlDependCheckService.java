package com.liuqi.etl.service.executors;

import com.liuqi.etl.bean.dto.EtlJobDTO;
import com.liuqi.etl.bean.dto.EtlJobDependDTO;
import com.liuqi.etl.bean.dto.EtlJobPublishedDTO;
import com.liuqi.etl.bean.dto.EtlJobStateDTO;
import com.liuqi.etl.service.EtlJobDependService;
import com.liuqi.etl.service.EtlJobPublishedService;
import com.liuqi.etl.service.EtlJobService;
import com.liuqi.etl.service.EtlJobStateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * 依赖任务检查处理服务
 *
 * @author LiuQi 2025/3/10-19:52
 * @version V1.0
 **/
@Service
@Slf4j
public class EtlDependCheckService {
    @Autowired
    private EtlJobDependService etlJobDependService;

    @Autowired
    private EtlJobStateService jobStateService;

    @Autowired
    @Lazy
    private EtlService etlService;

    @Autowired
    private EtlJobPublishedService jobService;

    // 单线程执行，防止并发时检测异常
    private static final ExecutorService executorService = Executors.newFixedThreadPool(1);

    /**
     * 触发依赖任务执行检查
     * @param finishedJobId 已执行完成的任务id
     */
    public void check(String finishedJobId, LocalDate dataDate) {
        executorService.submit(() -> {
            List<EtlJobDependDTO> jobDepends = etlJobDependService.findByParent(finishedJobId);
            if (CollectionUtils.isEmpty(jobDepends)) {
                return;
            }
            List<String> jobIds = jobDepends.stream().map(EtlJobDependDTO::getJobId).toList();

            try {
                List<EtlJobPublishedDTO> jobs = check(jobIds, dataDate, finishedJobId);
                jobs.forEach(job -> etlService.runJobAndSub(job, dataDate));
            } catch (Exception ex) {
                log.error("执行任务检查失败", ex);
            }
        });
    }

    /**
     * 检查任务
     *
     * @param jobIds   待检查的记录
     * @param dataDate 数据日期
     * @param finishedJobId 完成的任务id，可为空
     * @return 需要执行的任务清单
     */
    public List<EtlJobPublishedDTO> check(List<String> jobIds, LocalDate dataDate, @Nullable String finishedJobId) {
        if (CollectionUtils.isEmpty(jobIds)) {
            return new ArrayList<>(16);
        }
        
        // 查找所有待检任务的父任务
        List<String> parentIds = new ArrayList<>(16);
        Map<String, List<EtlJobDependDTO>> jobParentMap = etlJobDependService.findByJobs(jobIds)
                .stream()
                .peek(item -> parentIds.add(item.getParentJobId()))
                .collect(Collectors.groupingBy(EtlJobDependDTO::getJobId));

        if (CollectionUtils.isEmpty(parentIds)) {
            return new ArrayList<>(0);
        }

        // 查找每个父级节点的执行情况
        List<String> executedJobs = jobStateService.findByJobIds(parentIds, dataDate)
                .stream()
                .filter(item -> {
                    int state = item.getState();
                    return state == 2;
                })
                .map(EtlJobStateDTO::getJobId)
                .toList();

        // 查找任务信息
        List<EtlJobPublishedDTO> jobs = jobService.findByIds(jobIds)
                .stream()
                // 不自动执行的任务不需要处理
                .filter(EtlJobPublishedDTO::getAutoTrigger)
                .toList();

        List<EtlJobPublishedDTO> toExecuteJobs = new ArrayList<>(16);
        jobs.forEach(job -> {
            // 检查每一个任务的父任务，如果都已经执行，则需要执行该任务
            String jobId = job.getId();
            List<EtlJobDependDTO> parents = jobParentMap.get(jobId);
            if (CollectionUtils.isEmpty(parents)) {
                // 无父级节点 不自动触发执行
                return;
            }

            boolean notAllParentExecuted = parents.stream()
                    .map(EtlJobDependDTO::getParentJobId)
                    .anyMatch(item -> {
                        // 找父节点未执行的记录
                        return !item.equals(finishedJobId) && !executedJobs.contains(item);
                    });
            if (notAllParentExecuted) {
                // 存在父节点未执行，则继续等待
                return;
            }

            // 需要执行当前任务
            toExecuteJobs.add(job);
        });

        return toExecuteJobs;
    }
}

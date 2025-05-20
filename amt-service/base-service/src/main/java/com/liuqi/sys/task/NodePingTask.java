package com.liuqi.sys.task;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.liuqi.sys.service.ScheduleTaskExecutorService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 节点ping任务
 * 间隔一定时间往Redis中更新记录
 * 主要是解决定时任务执行时如何避免多节点同时执行的场景，定时任务只在主节点上执行
 *
 * @author  LiuQi 2025/5/16-21:02
 * @version V1.0
 **/
@Component
@Slf4j
public class NodePingTask {
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired(required = false)
    private Set<ScheduleTaskExecutorService> taskExecutorServices;

    private String nodeId;

    /**
     * 当前节点是否是主节点
     */
    public static boolean isMaster = false;

    @PostConstruct
    public void init() {
        this.nodeId = IdWorker.getIdStr();
    }

    @Scheduled(initialDelay = 10000, fixedDelay = 60000)
    public void ping() {
        // 更新节点是否主节点标志，每分钟执行一次，如果更新成功，说明主节点不存在，当前节点为主节点，需要启动相应的定时任务
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        Boolean b = valueOperations.setIfAbsent("masterNode", this.nodeId, 1, TimeUnit.MINUTES);
        if (null != b && b) {
            if (isMaster) {
                // 如果已经是主节点，不做处理
                return;
            }

            isMaster = true;

            // 启动所有定时任务
            if (null != taskExecutorServices) {
                log.info("节点为主节点，准备启动所有定时任务");
                taskExecutorServices.forEach(ScheduleTaskExecutorService::startAll);
            }
        }
    }
}

package com.liuqi.etl.service.executors.job;

import com.alibaba.fastjson2.JSON;
import com.liuqi.etl.bean.dto.EtlJobPublishedDTO;
import com.liuqi.etl.bean.dto.EtlLogDTO;
import com.liuqi.etl.service.EtlLogService;
import com.liuqi.etl.service.executors.config.EtlMqConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * MQTT监听任务处理
 *
 * @author LiuQi 2025/3/13-9:21
 * @version V1.0
 **/
@Service
@Slf4j
public class KafkaJob extends BaseMqJob {
    private static final Map<String, KafkaListener> clientMap = new Hashtable<>();

    // 上一条记录缓存，暂存储于内存中，需要落地到数据库或者是Redis中，避免系统重启时丢失 TODO
    private final Map<String, Object> cacheMap = new Hashtable<>(16);

    @Autowired
    private EtlLogService etlLogService;

    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(10);

    // 停止监听
    @Override
    public void stopListener(String jobId) {
        Optional.ofNullable(clientMap.get(jobId)).ifPresent(client -> {
            try {
                client.stop();
                clientMap.remove(jobId);
            } catch (Exception ex) {
                log.error("关闭连接失败", ex);
            }
        });
    }

    /**
     * 启动MQTT监听
     * @param job 作业信息
     * @param mqConfig mqtt配置
     */
    @Override
    public void startListener(EtlJobPublishedDTO job, EtlMqConfig mqConfig) throws Exception {
        // 需要判断之前的连接是否存在
        String jobId = job.getId();
        this.stopListener(jobId);

        Properties props = getProperties(job, mqConfig);

        // 创建 Kafka 消费者实例
        try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props)) {
            // 订阅主题
            consumer.subscribe(Arrays.asList(mqConfig.getTopic().split(",")));

            // 需要尝试拉一条数据，看连接是否成功
            consumer.poll(Duration.ofMillis(100));

            KafkaListener listener = new KafkaListener(consumer, job, mqConfig);
            listener.start();
            clientMap.put(jobId, listener);
        } catch (Exception e) {
            throw new Exception("Kafka连接失败");
        }
    }

    /**
     * 组织kafka配置
     */
    private static Properties getProperties(EtlJobPublishedDTO job, EtlMqConfig mqConfig) {
        String groupId = "amt_" + job.getId();

        // 配置 Kafka 消费者属性
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, mqConfig.getUrl());
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        return props;
    }

    private class KafkaListener {
        private final KafkaConsumer<String, String> consumer;
        private boolean stopped = false;
        private final EtlMqConfig config;
        private final EtlJobPublishedDTO job;
        private int retryTimes = 0;

        private final ExecutorService executorService;

        public KafkaListener(KafkaConsumer<String, String> consumer, EtlJobPublishedDTO job, EtlMqConfig config) {
            this.consumer = consumer;
            this.executorService = Executors.newFixedThreadPool(1);
            this.job = job;
            this.config = config;
        }

        private void consume() {
            while (!stopped) {
                ConsumerRecords<String, String> records;
                try {
                    records = consumer.poll(Duration.ofMillis(100));

                    if (retryTimes>0) {
                        // 重连成功
                        EtlLogDTO logDTO = EtlLogDTO.fromJob(job);
                        logDTO.setStatus(0);
                        logDTO.setErrorMsg("连接恢复");
                        etlLogService.insert(logDTO);
                    }
                    retryTimes = 0;
                } catch (Exception ex) {
                    if (retryTimes >= config.getMaxReconnectTimes()) {
                        // 不再处理
                        log.error("异常次数超限，退出处理");
                        try {
                            consumer.close();
                        } catch (Exception ignored){}
                        this.stopped = true;
                        clientMap.remove(job.getId());

                        EtlLogDTO dto = EtlLogDTO.fromJob(job);
                        dto.setStatus(1);
                        dto.setErrorMsg("Kafka连接失败重连超限，退出尝试");
                        etlLogService.insert(dto);

                        return;
                    }

                    retryTimes++;

                    // 否则进行重连
                    log.error("获取数据失败，尝试重连", ex);
                    // 最大重试延迟时间600S，也就是10分钟
                    scheduler.schedule(this::consume, Math.max(config.getReconnectDelay() * retryTimes, 600), TimeUnit.SECONDS);

                    if (retryTimes == 1) {
                        // 第一次重连保存日志，防止日志过多
                        EtlLogDTO dto = EtlLogDTO.fromJob(job);
                        dto.setStatus(1);
                        dto.setErrorMsg("Kafka连接失败，将尝试重连");
                        etlLogService.insert(dto);
                    }

                    return;
                }
                for (ConsumerRecord<String, String> record : records) {
                    String value = record.value();
                    if (StringUtils.isBlank(value)) {
                        continue;
                    }

                    Map<String, Object> obj = new HashMap<>(4);
                    obj.put("key", record.key());

                    Object valueObj = JSON.parse(value);
                    obj.put("value", valueObj);

                    try {
                        process(job, config, obj);
                    } catch (Exception ex) {
                        log.error("消息处理失败", ex);
                    }
                }
            }
        }

        public void start() {
            executorService.submit(this::consume);
        }

        public void stop() {
            this.stopped = true;
            consumer.close();
        }
    }
}

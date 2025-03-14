package com.liuqi.etl.service.executors.job;

import com.liuqi.common.base.bean.query.DynamicQuery;
import com.liuqi.etl.bean.dto.EtlJobPublishedDTO;
import com.liuqi.etl.service.EtlJobPublishedService;
import com.liuqi.etl.service.executors.config.EtlMqConfig;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * MQ服务
 *
 * @author LiuQi 2025/3/13-12:54
 * @version V1.0
 **/
@Service
@Slf4j
public class EtlMqJobService {
    @Autowired
    @Lazy
    private MqttJob mqttJob;

    @Autowired
    @Lazy
    private KafkaJob kafkaJob;

    @Autowired
    @Lazy
    private EtlJobPublishedService publishedService;

    @PostConstruct
    public void init() {
        // 系统重启时需要启动监听
        List<EtlJobPublishedDTO> list = publishedService.dynamicQuery(DynamicQuery.create().eq("type", "mq")).getRecords();
        list.forEach(job -> {
            try {
                this.startJob(job);
            } catch (Exception ex) {
                log.error("启动任务失败", ex);
            }
        });

        log.info("MQ任务自动启动完成");
    }

    public void startJob(EtlJobPublishedDTO job) {
        EtlMqConfig config = EtlMqConfig.parse(job.getConfig());
        String type = config.getType();
        if ("mqtt".equals(type)) {
            try {
                mqttJob.startListener(job, config);
            } catch (MqttException e) {
                throw new RuntimeException(e);
            }
        } else {
            try {
                kafkaJob.startListener(job, config);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void stopJob(String jobId) {
        mqttJob.stopListener(jobId);
    }
}

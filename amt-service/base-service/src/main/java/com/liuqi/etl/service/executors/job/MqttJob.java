package com.liuqi.etl.service.executors.job;

import com.liuqi.etl.bean.dto.EtlJobPublishedDTO;
import com.liuqi.etl.bean.dto.EtlLogDTO;
import com.liuqi.etl.service.EtlLogService;
import com.liuqi.etl.service.executors.config.EtlMqConfig;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Hashtable;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * MQTT监听任务处理
 *
 * @author  LiuQi 2025/3/13-9:21
 * @version V1.0
 **/
@Service
@Slf4j
public class MqttJob extends BaseMqJob{
    private static final String CLIENT_ID = "flowstone-amt";

    private static final Map<String, MqttClient> clientMap = new Hashtable<>();

    // 上一条记录缓存，暂存储于内存中，需要落地到数据库或者是Redis中，避免系统重启时丢失 TODO
    private final Map<String, Object> cacheMap = new Hashtable<>(16);

    @Autowired
    private EtlLogService etlLogService;

    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(10);

    // 停止监听
    @Override
    public void stopListener(String jobId) {
        Optional.ofNullable(clientMap.get(jobId)).ifPresent(client -> {
            if (!client.isConnected()) {
                clientMap.remove(jobId);
                return;
            }

            try {
                client.disconnect();
                clientMap.remove(jobId);
            } catch (MqttException e) {
                log.error("关闭连接失败", e);
            }
        });
    }

    /**
     * 客户端重连
     */
    private void reconnect(EtlJobPublishedDTO job, EtlMqConfig config, MqttClient client) {
        scheduler.schedule(() -> {
            try {
                client.connect();
                client.subscribe(config.getTopic());

                EtlLogDTO dto = EtlLogDTO.fromJob(job);
                dto.setStatus(0);
                dto.setErrorMsg("MQTT重新连接成功");
                etlLogService.insert(dto);
            } catch (Exception ex) {
                log.error("重连失败", ex);
                reconnect(job, config, client);
            }
        }, config.getReconnectDelay(), TimeUnit.SECONDS);
    }

    /**
     * 启动MQTT监听
     * @param job 作业信息
     * @param mqConfig mqtt配置
     */
    @Override
    public void startListener(EtlJobPublishedDTO job, EtlMqConfig mqConfig) throws MqttException {
        // 需要判断之前的连接是否存在
        String jobId = job.getId();
        this.stopListener(jobId);

        MqttClient client = new MqttClient(mqConfig.getUrl(), CLIENT_ID + "_" + jobId, new MemoryPersistence());
        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                EtlLogDTO dto = EtlLogDTO.fromJob(job);
                dto.setStatus(1);
                dto.setErrorMsg("MQTT连接断开，尝试重连中，异常信息：" + cause.getMessage());
                etlLogService.insert(dto);

                // 手动关闭时不会进这里
                reconnect(job, mqConfig, client);
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) {
                // 处理消息
                String msg = message.toString();
                try {
                    log.debug("开始处理mqtt消息：{}", job.getName());
                    process(job, mqConfig, msg);
                    log.debug("MQTT消息处理完成：{}", job.getName());
                } catch (Exception ex) {
                    log.error("mqtt消息处理失败", ex);
                    EtlLogDTO logDTO = EtlLogDTO.fromJob(job);
                    logDTO.setDataDate(LocalDate.now());
                    logDTO.setErrorMsg(ex.getMessage());
                    logDTO.setStatus(1);
                    etlLogService.insert(logDTO);
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });

        MqttConnectOptions connOpts = new MqttConnectOptions();
        connOpts.setCleanSession(true);
        connOpts.setConnectionTimeout(10);
        connOpts.setAutomaticReconnect(false);
        client.connect(connOpts);
        clientMap.put(jobId, client);

        // 订阅主题
        client.subscribe(mqConfig.getTopic());
    }
}

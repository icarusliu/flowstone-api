package com.liuqi.etl.service.executors.job;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.dynamic.datasource.toolkit.DynamicDataSourceContextHolder;
import com.liuqi.common.utils.DynamicSqlHelper;
import com.liuqi.common.utils.GroovyUtils;
import com.liuqi.common.utils.JsUtils;
import com.liuqi.etl.bean.dto.EtlJobPublishedDTO;
import com.liuqi.etl.bean.dto.EtlLogDTO;
import com.liuqi.etl.service.EtlLogService;
import com.liuqi.etl.service.executors.config.EtlMqConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * MQTT监听任务处理
 *
 * @author  LiuQi 2025/3/13-9:21
 * @version V1.0
 **/
@Service
@Slf4j
public class MqttJobService {
    private static final String CLIENT_ID = "flowstone-amt";

    private static final Map<String, MqttClient> clientMap = new Hashtable<>();

    // 缓存，暂存储于内存中，需要落地到数据库或者是Redis中，避免系统重启时丢失 TODO
    private final Map<String, Object> cacheMap = new Hashtable<>(16);

    @Autowired
    private EtlLogService etlLogService;

    // 停止监听
    public void stopListener(String jobId) {
        Optional.ofNullable(clientMap.get(jobId)).ifPresent(client -> {
            try {
                client.close(true);
            } catch (MqttException e) {
                log.error("关闭连接失败", e);
            }
        });
    }

    /**
     * 启动MQTT监听
     * @param dto 作业信息
     * @param mqConfig mqtt配置
     */
    public void startListener(EtlJobPublishedDTO dto, EtlMqConfig mqConfig) throws MqttException {
        // 需要判断之前的连接是否存在
        String jobId = dto.getId();
        this.stopListener(jobId);

        MqttClient client = new MqttClient(mqConfig.getUrl(), CLIENT_ID + "_" + jobId, new MemoryPersistence());
        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                // 需要处理失联后自动重连 TODO
                clientMap.remove(jobId);
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) {
                // 处理消息
                String msg = message.toString();
                try {
                    log.debug("开始处理mqtt消息：{}", dto.getName());
                    process(jobId, mqConfig, msg);
                    log.debug("MQTT消息处理完成：{}", dto.getName());
                } catch (Exception ex) {
                    log.error("mqtt消息处理失败", ex);
                    EtlLogDTO logDTO = new EtlLogDTO();
                    logDTO.setJobId(dto.getId());
                    logDTO.setJobName(dto.getName());
                    logDTO.setJobCode(dto.getCode());
                    logDTO.setExecuteTime(LocalDateTime.now());
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
        client.connect(connOpts);
        clientMap.put(jobId, client);

        // 订阅主题
        client.subscribe(mqConfig.getTopic());
    }

    /**
     * 处理消息内容
     *
     * @param mqConfig 处理配置
     * @param msg      接收到的消息
     */
    private void process(String jobId, EtlMqConfig mqConfig, String msg) {
        if (StringUtils.isBlank(msg)) {
            return;
        }
        Object obj = JSON.parse(msg);

        // 需要将结果转换
        String script = mqConfig.getScript();
        if (StringUtils.isNotBlank(script)) {
            String scriptType = mqConfig.getScriptType();
            if ("js".equals(scriptType) || "javascript".equals(scriptType)) {
                obj = JsUtils.execute(script, obj);
            } else {
                obj = GroovyUtils.execute(script, obj);
            }

            if (resultNull(obj)) {
                // 不需要处理的消息
                return;
            }

            obj = JSON.parse(JSON.toJSONString(obj));
        }

        // 只处理两种情况，一是数组的情况；二是数据对象的情况
        List<Map<String, Object>> list = new ArrayList<>(16);
        if (obj instanceof JSONObject jsonObject) {
            Map<String, Object> result = this.compute(jobId, mqConfig, jsonObject);
            if (null != result) {
                list.add(result);
            }
        } else {
            JSONArray array = (JSONArray) obj;
            for (int i = 0; i < array.size(); i++) {
                JSONObject jsonObject = array.getJSONObject(i);
                Map<String, Object> result = this.compute(jobId, mqConfig, jsonObject);
                if (null != result) {
                    list.add(result);
                }
            }
        }

        if (!CollectionUtils.isEmpty(list)) {
            // 调用写入SQL
            String sql = mqConfig.getDestSql();
            String ds = mqConfig.getDestDs();
            if (StringUtils.isNotBlank(ds)) {
                DynamicDataSourceContextHolder.push(ds);
            }
            try {
                DynamicSqlHelper.batchInsert(jobId, sql, list);
            }finally {
                if (StringUtils.isNotBlank(ds)) {
                    DynamicDataSourceContextHolder.poll();
                }
            }
        }
    }

    private static Boolean resultNull(Object obj) {
        return null == obj || obj.equals("null") || obj.equals("false") || obj.equals(false);
    }

    /**
     * 数据计算
     */
    private Map<String, Object> compute(String jobId, EtlMqConfig config, JSONObject obj) {
        // 进行计算
        if (StringUtils.isNotBlank(config.getComputeScript())) {
            // 检查是否需要存储历史记录
            Object lastValue = null;
            if (config.getCacheLast()) {
                String key = config.getCacheKey();
                String value = jobId;
                if (StringUtils.isNotBlank(key)) {
                    value = obj.getString(key);
                }
                if (!StringUtils.isBlank(value)) {
                    String cacheKey = jobId + "_" + value;
                    lastValue = cacheMap.get(cacheKey);
                    // 缓存
                    cacheMap.put(cacheKey, obj);
                }
            }

            // 进行计算
            String scriptType = config.getComputeScriptType();
            Object result;
            if ("js".equals(scriptType) || "javascript".equals(scriptType)) {
                result = JsUtils.execute(config.getComputeScript(), obj, lastValue);
            } else {
                result = GroovyUtils.execute(config.getComputeScript(), obj, lastValue);
            }

            if (resultNull(result)) {
                return null;
            }

            obj = JSON.parseObject(JSON.toJSONString(result));
        }

        return obj;
    }

    public static void main(String[] args) {
        Map<String, Object> map = new HashMap<>(16);
        map.put("a", 1);
        Object obj = JSON.parse(JSON.toJSONString(map));
        System.out.println(obj);
    }
}

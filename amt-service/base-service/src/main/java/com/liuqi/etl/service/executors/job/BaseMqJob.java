package com.liuqi.etl.service.executors.job;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.dynamic.datasource.toolkit.DynamicDataSourceContextHolder;
import com.liuqi.common.utils.DynamicSqlHelper;
import com.liuqi.common.utils.GroovyUtils;
import com.liuqi.common.utils.JsUtils;
import com.liuqi.etl.bean.dto.EtlJobPublishedDTO;
import com.liuqi.etl.service.executors.config.EtlMqConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * MQTT监听任务处理
 *
 * @author  LiuQi 2025/3/13-9:21
 * @version V1.0
 **/
@Slf4j
public abstract class BaseMqJob {
    // 上一条记录缓存，暂存储于内存中，需要落地到数据库或者是Redis中，避免系统重启时丢失 TODO
    private final Map<String, Object> cacheMap = new Hashtable<>(16);

    // 停止监听
    public abstract void stopListener(String jobId);

    /**
     * 启动MQTT监听
     * @param job 作业信息
     * @param mqConfig mqtt配置
     */
    public abstract void startListener(EtlJobPublishedDTO job, EtlMqConfig mqConfig) throws Exception;

    /**
     * 处理消息内容
     *
     * @param mqConfig 处理配置
     * @param msg      接收到的消息
     */
    protected void process(EtlJobPublishedDTO job, EtlMqConfig mqConfig, Object msg) {
        if (null == msg) {
            return;
        }
        String jobId = job.getId();
        Object obj = msg;
        if (msg instanceof String str) {
            obj = JSON.parse(str);
        }

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

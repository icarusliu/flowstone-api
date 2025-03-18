package com.liuqi.etl.service.executors.config;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson2.JSONObject;
import lombok.Data;
import org.springframework.lang.Nullable;

import java.util.Map;

/**
 * MQ配置
 *
 * @author  LiuQi 2025/3/13-9:28
 * @version V1.0
 **/
@Data
public class EtlMqConfig {
    private String type;
    private String url;
    private String topic;
    private String scriptType;
    @Nullable private String script;
    private String computeScriptType;
    @Nullable private String computeScript;
    private Boolean cacheLast;
    @Nullable private String cacheKey;

    // 目标类型，模型或sql或其它mq
    private String destType;

    private String destDs;
    private String destSql;
    private String destModel;

    /**
     * 重连间隔
     */
    private Integer reconnectDelay = 30;

    /**
     * 最大重连次数，30次，每次延迟时间根据重试次数增加，第一次30S，第二次60S，第三次120S，至最多600S；
     */
    private Integer maxReconnectTimes = 30;

    public static EtlMqConfig parse(Map<String, Object> config) {
        return JSONObject.parseObject(JSON.toJSONString(config), EtlMqConfig.class);
    }
}

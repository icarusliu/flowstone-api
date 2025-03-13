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
    private String destDs;
    private String destSql;

    public static EtlMqConfig parse(Map<String, Object> config) {
        return JSONObject.parseObject(JSON.toJSONString(config), EtlMqConfig.class);
    }
}

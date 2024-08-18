package com.liuqi.base.service;

import com.liuqi.base.bean.dto.ScheduleTaskDTO;
import com.liuqi.common.ErrorCodes;
import com.liuqi.common.exception.AppException;
import com.liuqi.dua.bean.dto.ApiDTO;
import com.liuqi.dua.service.ApiService;
import com.liuqi.dua.service.DuaService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.StringSubstitutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.CronTask;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 任务执行器
 *
 * @author LiuQi 2024/8/16-15:57
 * @version V1.0
 **/
@Component
@Slf4j
public class ScheduleTaskExecutor {
    @Autowired
    private ScheduleTaskService taskService;

    @Autowired
    private ApiService apiService;

    @Autowired
    private ThreadPoolTaskScheduler threadPoolTaskScheduler;

    @Autowired
    private DuaService duaService;

    private final Map<String, ScheduledFuture<?>> scheduledFutureMap = new ConcurrentHashMap<>(16);
    private final Pattern pattern = Pattern.compile("(?<=\\$\\{).*?(?=})");

    /**
     * 启动所有任务
     */
    @PostConstruct
    public void startAll() {
        taskService.findAll().forEach(task -> {
            if (task.getStarted() || task.getStatus() == 0) {
                return;
            }

            startTask(task.getId());
        });
    }

    /**
     * 停止单个任务
     *
     * @param id 任务id
     */
    public void stopTask(String id) {
        Optional.ofNullable(scheduledFutureMap.get(id)).ifPresent(future -> future.cancel(true));
        taskService.updateStarted(id, false);
        log.info("任务停止成功");
    }

    /**
     * 重启某个定时任务
     *
     * @param id 任务id
     */
    public void restartTask(String id) {
        this.stopTask(id);
        this.startTask(id);
    }

    /**
     * 启动单个任务
     *
     * @param id 任务id
     */
    @Transactional
    public void startTask(String id) {
        ScheduleTaskDTO taskInfo = taskService.findById(id).orElse(null);
        if (null == taskInfo) {
            return;
        }

        boolean b = taskService.updateStarted(id, true);
        if (!b) {
            // 更新失败，说明任务已经启动完成
            return;
        }

        String apiId = taskInfo.getApiId();
        ApiDTO api = apiService.findById(apiId).orElseThrow(() -> AppException.of(ErrorCodes.API_NOT_EXISTS));

        CronTask cronTask = new CronTask(createRunnable(api, taskInfo.getParams()), taskInfo.getCron());
        ScheduledFuture<?> future = threadPoolTaskScheduler.schedule(cronTask.getRunnable(), cronTask.getTrigger());
        scheduledFutureMap.put(id, future);

        log.info("任务启动成功");
    }

    /**
     * 创建接口调用器
     *
     * @param api    接口信息
     * @param params 调用参数
     * @return 处理结果
     */
    private Runnable createRunnable(ApiDTO api, Map<String, Object> params) {
        return () -> {
            // 调用API
            Map<String, Object> finalParams = Optional.ofNullable(params).orElse(new HashMap<>(0));

            LocalDateTime nowTime = LocalDateTime.now();

            // 需要进行参数变量替换，只处理第一层参数
            Map<String, Object> paramValue = new HashMap<>(16);
            paramValue.put("yyyy-MM-dd", nowTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            paramValue.put("nowDate", nowTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            paramValue.put("yyyyMMdd", nowTime.format(DateTimeFormatter.ofPattern("yyyyMMdd")));
            paramValue.put("yyyyMM", nowTime.format(DateTimeFormatter.ofPattern("yyyyMM")));
            paramValue.put("yyyy-MM", nowTime.format(DateTimeFormatter.ofPattern("yyyy-MM")));
            paramValue.put("yyyy", nowTime.format(DateTimeFormatter.ofPattern("yyyy")));
            paramValue.put("yyyy-MM-dd HH:mm:ss", nowTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            paramValue.put("nowTime", nowTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            paramValue.put("yyyy-MM-dd HH:mm", nowTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
            StringSubstitutor ss = new StringSubstitutor(paramValue);

            LocalDate nowDate = LocalDate.now();

            finalParams.forEach((k, v) -> {
                if (null == v) {
                    return;
                }

                String value = v.toString();
                if (!value.contains("${")) {
                    return;
                }

                value = ss.replace(value);

                // 支持${-7 days yyyy-MM-dd HH:mm:ss}这种变量
                Matcher matcher = pattern.matcher(value);
                Map<String, String> vMap = new HashMap<>(16);
                matcher.results().forEach(result -> {
                    String variable = result.group();
                    String[] arr = variable.trim().split("\\s+");
                    if (arr.length > 4) {
                        // 参数只支持4个，第一个为数量，第二个为类型，第3/4个为格式，比如yyyy-MM-dd HH:mm:ss
                        return;
                    }

                    String type = arr[1];
                    String format = getVariableFormat(arr, type);

                    int num = Integer.parseInt(arr[0]);
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
                    String vValue = switch (type) {
                        case "years" -> nowDate.plusYears(num).format(formatter);
                        case "days" -> nowDate.plusDays(num).format(formatter);
                        case "months" -> nowDate.plusMonths(num).format(formatter);
                        case "hours" -> nowTime.plusHours(num).format(formatter);

                        default -> null;
                    };

                    if (null != vValue) {
                        vMap.put(variable, vValue);
                    }
                });

                if (!vMap.isEmpty()) {
                    StringSubstitutor ss1 = new StringSubstitutor(vMap);
                    value = ss1.replace(value);
                }

                finalParams.put(k, value);
            });

            duaService.execute(api.getId(), finalParams);
        };
    }

    /**
     * 获取变量格式
     * 比如变量：-5 days yyyy-MM-dd HH:mm:ss，获取最后的yyyy开头至结尾的所有格式
     * 格式可能没传，也可能只传了日期格式
     *
     * @param arr  变量分隔数组
     * @param type 变量类型
     * @return 格式化
     */
    private static String getVariableFormat(String[] arr, String type) {
        String format = null;
        if (arr.length == 4) {
            format = arr[2] + " " + arr[3];
        } else if (arr.length == 3) {
            format = arr[2];
        }

        if (null == format) {
            if (Objects.equals(type, "years") || Objects.equals(type, "days") || Objects.equals(type, "months")) {
                format = "yyyy-MM-dd";
            } else {
                format = "yyyy-MM-dd HH:mm:ss";
            }
        }
        return format;
    }

    public static void main(String[] args) {

    }
}

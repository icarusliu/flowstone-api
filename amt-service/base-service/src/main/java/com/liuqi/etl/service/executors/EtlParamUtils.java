package com.liuqi.etl.service.executors;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * 参数工具类
 *
 * @author  LiuQi 2025/3/10-19:22
 * @version V1.0
 **/
public class EtlParamUtils {
    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * 组装参数
     * @param dataDate 数据日期
     * @return 组装好的参数
     */
    public static Map<String, Object> getEtlParams(LocalDate dataDate) {
        Map<String, Object> params = new HashMap<>(16);
        // 数据日期
        params.put("dataDate", dataDate.format(FORMATTER));
        // 后一天
        params.put("nextDate", dataDate.plusDays(1).format(FORMATTER));
        // 数据月份
        params.put("dataMonth", dataDate.format(DateTimeFormatter.ofPattern("yyyy-MM")));
        params.put("month", dataDate.getMonthValue());
        // 月初
        params.put("monthStart", dataDate.format(DateTimeFormatter.ofPattern("yyyy-MM-01")));
        // 月末
        params.put("monthEnd", dataDate.withDayOfMonth(1).plusMonths(1).minusDays(1).format(FORMATTER));
        // 年份
        params.put("year", dataDate.getYear());
        params.put("dataYear", dataDate.getYear());
        params.put("yearStart", dataDate.getYear() + "-01-01");
        params.put("yearEnd", dataDate.getYear() + "-12-31");
        // 上一年
        params.put("lastYear", dataDate.minusYears(1).getYear());
        // 上一年开始日期
        params.put("lastYearStart", dataDate.minusYears(1).getYear() + "-01-01");
        return params;
    }
}

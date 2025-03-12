package com.liuqi.etl.web;

import com.liuqi.etl.service.executors.EtlService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * 任务控制器
 *
 * @author LiuQi 2024/8/6-18:05
 * @version V1.0
 **/
@RestController
@RequestMapping("etl")
@Slf4j
public class EtlController {
    @Autowired
    private EtlService etlService;
    
    @GetMapping("/test")
    public String runJob(String jobId, String dataDate) {
        LocalDate date = LocalDate.parse(dataDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        try {
            etlService.testJob(jobId, date);
            return "成功";
        } catch (Exception ex) {
            log.error("任务测试异常", ex);
            return ex.getMessage();
        }
    }
}

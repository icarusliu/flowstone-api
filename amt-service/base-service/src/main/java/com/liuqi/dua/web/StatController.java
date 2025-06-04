package com.liuqi.dua.web;

import com.baomidou.mybatisplus.extension.toolkit.SqlRunner;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 统计控制器
 *
 * @author  LiuQi 2025/6/4-13:32
 * @version V1.0
 **/
@RestController
@RequestMapping("/base/stat")
public class StatController {
    /**
     * 按月统计，查询最近12个月数据
     */
    @GetMapping("month")
    public List<Map<String, Object>> statByMonth() {
        String sql = """
                select date_format(create_time, '%Y-%m') as dataMonth, count(1) as total, sum(status) as failed from d_api_log group by date_format(create_time, '%Y-%m')
                order by dataMonth desc limit 12
               """;
        return SqlRunner.db().selectList(sql);
    }
}

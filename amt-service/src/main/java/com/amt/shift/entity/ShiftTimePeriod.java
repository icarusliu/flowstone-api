package com.amt.shift.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalTime;
import java.time.LocalDateTime;

@Data
@TableName("shift_time_period")
public class ShiftTimePeriod {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long shiftId;
    private String periodName;
    private LocalTime startTime;
    private LocalTime endTime;
    private Integer periodType;
    private String description;
    private String createdBy;
    private LocalDateTime createdTime;
    private String updatedBy;
    private LocalDateTime updatedTime;
}

-- 班次基础信息表
CREATE TABLE shift_info (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    shift_code VARCHAR(50) NOT NULL COMMENT '班次编码',
    shift_name VARCHAR(100) NOT NULL COMMENT '班次名称',
    start_time TIME NOT NULL COMMENT '班次开始时间',
    end_time TIME NOT NULL COMMENT '班次结束时间',
    description VARCHAR(255) COMMENT '班次描述',
    is_cross_day TINYINT(1) DEFAULT 0 COMMENT '是否跨天 0-否 1-是',
    status TINYINT(1) DEFAULT 1 COMMENT '状态 0-禁用 1-启用',
    created_by VARCHAR(50) COMMENT '创建人',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_by VARCHAR(50) COMMENT '更新人',
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_shift_code (shift_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='班次基础信息表';

-- 班次时间段表
CREATE TABLE shift_time_period (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    shift_id BIGINT NOT NULL COMMENT '班次ID',
    period_name VARCHAR(100) NOT NULL COMMENT '时间段名称',
    start_time TIME NOT NULL COMMENT '开始时间',
    end_time TIME NOT NULL COMMENT '结束时间',
    period_type TINYINT(1) DEFAULT 1 COMMENT '时间段类型 1-工作时间 2-休息时间',
    description VARCHAR(255) COMMENT '描述',
    created_by VARCHAR(50) COMMENT '创建人',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_by VARCHAR(50) COMMENT '更新人',
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    KEY idx_shift_id (shift_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='班次时间段表';

-- 班次排班表
CREATE TABLE shift_schedule (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    shift_id BIGINT NOT NULL COMMENT '班次ID',
    employee_id VARCHAR(50) NOT NULL COMMENT '员工ID',
    schedule_date DATE NOT NULL COMMENT '排班日期',
    status TINYINT(1) DEFAULT 1 COMMENT '状态 1-正常 2-调休 3-请假',
    created_by VARCHAR(50) COMMENT '创建人',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_by VARCHAR(50) COMMENT '更新人',
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    KEY idx_employee_date (employee_id, schedule_date),
    KEY idx_shift_date (shift_id, schedule_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='班次排班表';

-- 插入示例数据
INSERT INTO shift_info (shift_code, shift_name, start_time, end_time, description, is_cross_day) VALUES
('MORNING', '早班', '08:00:00', '16:00:00', '标准早班时间', 0),
('MIDDLE', '中班', '16:00:00', '00:00:00', '标准中班时间', 0),
('NIGHT', '夜班', '00:00:00', '08:00:00', '标准夜班时间', 1);

-- 插入示例时间段
INSERT INTO shift_time_period (shift_id, period_name, start_time, end_time, period_type, description) VALUES
(1, '上午工作时间', '08:00:00', '12:00:00', 1, '上午工作时段'),
(1, '午休时间', '12:00:00', '13:00:00', 2, '午休时段'),
(1, '下午工作时间', '13:00:00', '16:00:00', 1, '下午工作时段');

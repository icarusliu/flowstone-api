package com.liuqi.base.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.liuqi.common.base.domain.entity.BaseEntity;
import lombok.Data;

@Data
@TableName("base_sys_config")
public class SysConfigEntity extends BaseEntity {
    private String code;

    private String value;

    private String name;

    private String remark;

    private Boolean enabled;
}

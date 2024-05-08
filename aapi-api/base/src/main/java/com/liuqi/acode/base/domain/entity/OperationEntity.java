package com.liuqi.acode.base.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.liuqi.common.base.domain.entity.BaseEntity;
import lombok.Data;

@TableName("base_operation")
@Data
public class OperationEntity extends BaseEntity {
    private String code;

    private String name;

    private String remark;

    private String menuId;

    private Integer sort;

    private Boolean enabled;
}

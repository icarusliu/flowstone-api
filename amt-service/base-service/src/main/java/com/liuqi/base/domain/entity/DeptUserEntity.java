package com.liuqi.base.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.liuqi.common.base.domain.entity.BaseEntity;
import lombok.Data;

@Data
@TableName("base_dept_user")
public class DeptUserEntity extends BaseEntity {
    private String deptId;

    private String userId;
}

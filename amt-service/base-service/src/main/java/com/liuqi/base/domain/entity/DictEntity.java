package com.liuqi.base.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.liuqi.common.base.domain.entity.BaseEntity;
import lombok.Data;

/**
 * 字典
 */
@Data
@TableName("base_dict")
public class DictEntity extends BaseEntity {
    private String code;

    private String name;

    private String remark;
}

package com.liuqi.base.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.liuqi.common.base.domain.entity.BaseEntity;
import lombok.Data;

/**
 * 字典项
 */
@Data
@TableName("base_dict_item")
public class DictItemEntity extends BaseEntity {
    private String dictId;

    private String code;

    private String name;

    private String value;

    private String remark;
}

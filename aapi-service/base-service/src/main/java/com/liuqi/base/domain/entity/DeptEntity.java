package com.liuqi.base.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.liuqi.common.annotations.Comment;
import com.liuqi.common.base.domain.entity.BaseEntity;
import lombok.Data;

@Data
@TableName("base_dept")
@Comment("机构")
public class DeptEntity extends BaseEntity {
    @Comment("编码")
    private String code;

    @Comment("名称")
    private String name;

    @Comment("父级机构")
    private String parentId;

    @Comment("排序")
    private Integer sort;
}

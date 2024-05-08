package com.liuqi.acode.base.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.liuqi.acode.base.bean.dto.MenuType;
import com.liuqi.common.base.domain.entity.BaseEntity;
import lombok.Data;

@Data
@TableName("base_menu")
public class MenuEntity extends BaseEntity {
    private String code;

    private String name;

    private String path;

    private MenuType type;

    private Boolean show;

    private String parentId;

    private Integer sort;
}

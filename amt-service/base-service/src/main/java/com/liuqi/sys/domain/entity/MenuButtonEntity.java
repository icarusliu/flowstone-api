package com.liuqi.sys.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.liuqi.common.annotations.Comment;
import com.liuqi.common.base.domain.entity.BaseEntity;
import lombok.Data;

/**
 * 菜单按钮
 *
 * @author  LiuQi 2024/9/29-18:52
 * @version V1.0
 **/
@Data
@TableName("sys_menu_button")
@Comment("菜单按钮")
public class MenuButtonEntity extends BaseEntity {
    @Comment("按钮名称")
    private String name;

    @Comment("按钮编码")
    private String code;

    @Comment("菜单id")
    private String menuId;

    @Comment("排序")
    private Integer sort;
}

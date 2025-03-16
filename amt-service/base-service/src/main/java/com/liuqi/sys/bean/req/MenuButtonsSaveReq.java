package com.liuqi.sys.bean.req;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 菜单按钮保存
 *
 * @author  LiuQi 2024/9/30-13:36
 * @version V1.0
 **/
@Data
public class MenuButtonsSaveReq {
    @NotEmpty
    private String menuId;

    @NotNull
    private List<MenuButtonAddReq> buttons;
}

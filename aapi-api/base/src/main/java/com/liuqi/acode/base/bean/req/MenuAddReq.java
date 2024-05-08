package com.liuqi.acode.base.bean.req;

import com.liuqi.acode.base.bean.dto.MenuType;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class MenuAddReq {
    private String code;
    @NotBlank
    private String name;

    @NotBlank
    private String path;

    private MenuType type = MenuType.PATH;
    private Boolean show = true;
    private String parentId = "-1";
    private Integer sort = 0;
}
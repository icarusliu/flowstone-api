package com.liuqi.sys.bean.req;

import com.liuqi.sys.bean.dto.MenuType;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class MenuAddReq {
    private String code;
    @NotBlank
    private String name;

    @NotBlank
    private String path;

    private String icon;

    private MenuType type = MenuType.PATH;
    private Boolean show = true;
    private String parentId = "-1";
    private Integer sort = 0;
}
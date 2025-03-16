package com.liuqi.base.bean.req;

import com.liuqi.base.bean.dto.MenuType;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class MenuUpdateReq {
    @NotBlank
    private String id;

    private String code;
    private String icon;
    private String name;

    private String path;

    private MenuType type;
    private Boolean hide;
    private String parentId;
    private Integer sort;
}
package com.liuqi.base.bean.req;

import com.liuqi.base.bean.dto.MenuType;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;

@Data
public class MenuUpdateReq {
    @NotBlank
    private String id;

    private String code;
    private String name;

    private String path;

    private MenuType type;
    private Boolean show;
    private String parentId;
    private Integer sort;
}
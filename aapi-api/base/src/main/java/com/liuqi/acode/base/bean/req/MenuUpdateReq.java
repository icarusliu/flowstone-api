package com.liuqi.acode.base.bean.req;

import com.liuqi.acode.base.bean.dto.MenuType;
import lombok.Data;

import javax.validation.constraints.NotBlank;

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
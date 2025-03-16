package com.liuqi.base.bean.req;

import com.liuqi.base.bean.DictItem;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
public class DictUpdateReq {
    @NotBlank
    private String id;

    private String code;

    private String name;

    private Integer status;

    private List<DictItem> items;

    private String remark;
}
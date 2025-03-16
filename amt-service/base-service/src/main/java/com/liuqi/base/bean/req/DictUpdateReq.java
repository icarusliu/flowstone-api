package com.liuqi.base.bean.req;

import com.liuqi.base.bean.DictItem;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class DictUpdateReq {
    /**
     * 字典类型，items：列表字段，sql: sql字典
     */
    private String type;

    @NotBlank
    private String id;

    private String code;

    private String name;

    private Integer status;

    private List<DictItem> items;

    private String remark;

    private Map<String, Object> metadata;
}
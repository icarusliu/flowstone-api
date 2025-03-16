package com.liuqi.sys.bean.req;

import com.liuqi.sys.bean.DictItem;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class DictAddReq {
    /**
     * 字典类型，items：列表字段，sql: sql字典
     */
    private String type;

    @NotBlank
    private String code;

    @NotBlank
    private String name;

    private String remark;

    private List<DictItem> items;

    private Map<String, Object> metadata;
}
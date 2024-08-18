package com.liuqi.dua.executor.bean;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;

import java.util.List;

/**
 * 输入参数
 *
 * @author  LiuQi 2024/8/15-21:41
 * @version V1.0
 **/
@Data
public class InputParam {
    private String code;
    private String name;
    private String type;
    private Boolean required;
    @JSONField(name = "default")
    private String defaultValue;
    private String remark;

    private List<InputParam> children;
}

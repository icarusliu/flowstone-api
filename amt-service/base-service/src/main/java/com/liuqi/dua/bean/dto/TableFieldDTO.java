package com.liuqi.dua.bean.dto;

import lombok.Data;

/**
 * 表字段
 *
 * @author  LiuQi 2024/9/26-15:58
 * @version V1.0
 **/
@Data
public class TableFieldDTO {
    /**
     * 表字段编码
     */
    private String field;

    /**
     * 表字段类型
     */
    private String type;
}

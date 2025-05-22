package com.liuqi.common.base.bean.query;

import lombok.Data;

/**
 * 排序
 *
 * @author  LiuQi 2025/5/22-16:15
 * @version V1.0
 **/
@Data
public class OrderBy {
    /**
     * 是否升序
     */
    private boolean asc = true;

    /**
     * 排序字段
     */
    private String column;
}

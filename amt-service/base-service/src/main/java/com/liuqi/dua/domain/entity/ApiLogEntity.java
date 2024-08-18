package com.liuqi.dua.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.liuqi.common.annotations.Comment;
import com.liuqi.common.base.domain.entity.BaseEntity;
import lombok.Data;

/**
 * 接口执行日志
 *
 * @author  LiuQi 2024/8/14-14:19
 * @version V1.0
 **/
@Data
@Comment("接口执行日志")
@TableName("d_api_log")
public class ApiLogEntity extends BaseEntity {
    private String apiId;
    private String apiPath;
    private String apiName;
    private int status = 0;

    private Integer spentTime;

    @Comment("接口调用参数")
    private String params;

    @Comment("简短异常信息")
    private String errorMsg;

    @Comment("存储结果或异常详细信息")
    private String result;
}

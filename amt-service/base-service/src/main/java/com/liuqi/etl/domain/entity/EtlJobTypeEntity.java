package com.liuqi.etl.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.liuqi.common.annotations.Comment;
import com.liuqi.common.base.domain.entity.BaseEntity;
import lombok.Data;

/**
 * 任务分类
 *
 * @author  LiuQi 2025/3/10-14:18
 * @version V1.0
 **/
@Data
@TableName("b_etl_job_type")
public class EtlJobTypeEntity extends BaseEntity {
    /**
     * 分类编码
     */
    private String code;

    @Comment("分类名称")
    private String name;

    @Comment("分类排序")
    private String sort;

    @Comment("父分类id")
    private String parentId;

    @Comment("说明")
    private String remark;
}

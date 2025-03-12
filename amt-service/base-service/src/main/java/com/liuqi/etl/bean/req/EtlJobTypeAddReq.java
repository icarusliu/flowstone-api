package com.liuqi.etl.bean.req;

import lombok.Data;

/**
 * 任务分类新增对象 
 * @author Coder Generator 2025-03-10 15:11:42 
 **/
@Data
public class EtlJobTypeAddReq {
    /**
     *分类编码
     **/
    private String code;
    /**
     *分类名称
     **/
    private String name;
    /**
     *分类排序
     **/
    private String sort;
    /**
     *父分类id
     **/
    private String parentId;
    /**
     *说明
     **/
    private String remark;
}
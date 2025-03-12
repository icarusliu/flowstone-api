package com.liuqi.etl.bean.req;

import lombok.Data;

/**
 * 任务分类更新对象 
 * @author Coder Generator 2025-03-10 15:11:42 
 **/
@Data
public class EtlJobTypeUpdateReq {
    private String id;
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
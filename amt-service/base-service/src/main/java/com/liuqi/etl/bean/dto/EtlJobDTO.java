package com.liuqi.etl.bean.dto;

import lombok.Data;

import java.util.List;

/**
 * ETL任务数据实体 
 * @author Coder Generator 2025-03-10 16:40:38 
 **/
@Data
public class EtlJobDTO extends EtlJobPublishedDTO {
    /**
     * 分类id
     */
    private String typeId;
    /**
     * 是否启用
     */
    private Boolean enabled;
    /**
     * 排序序号
     */
    private String sort;

    private List<String> usedTables;

    /**
     * 更新的表清单
     */
    private List<String> updatedTables;

    /**
     * 任务版本
     */
    private Integer version;

    /**
     * 依赖的任务
     */
    private List<EtlJobDependDTO> depends;
}
package com.liuqi.dua.domain.mapper; /**
 * 接口草稿数据库操作服务
 *
 * @author Coder Generator 2024-07-08 22:33:46
 **/

import com.liuqi.common.base.domain.mapper.BaseMapper;
import com.liuqi.dua.domain.entity.ApiDraftEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface ApiDraftMapper extends BaseMapper<ApiDraftEntity> {
    /**
     * 根据状态统计
     */
    List<Map<String, Object>> statByStatus();
}
package com.liuqi.dua.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.liuqi.common.base.service.AbstractBaseEntityService;
import com.liuqi.dua.bean.dto.ApiHistoryDTO;
import com.liuqi.dua.bean.query.ApiHistoryQuery;
import com.liuqi.dua.domain.entity.ApiHistoryEntity;
import com.liuqi.dua.domain.mapper.ApiHistoryMapper;
import com.liuqi.dua.service.ApiHistoryEntityService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

/**
 * 接口历史服务实现 
 * @author Coder Generator 2025-05-23 08:52:24 
 **/
@Service
public class ApiHistoryEntityServiceImpl extends AbstractBaseEntityService<ApiHistoryEntity, ApiHistoryDTO, ApiHistoryMapper, ApiHistoryQuery> implements ApiHistoryEntityService {
    @Override
    protected QueryWrapper<ApiHistoryEntity> queryToWrapper(ApiHistoryQuery query) {
        return this.createQueryWrapper()
                .eq(StringUtils.isNotBlank(query.getId()), "id", query.getId())
                .in(null != query.getIds(), "id", query.getIds())
                .orderByDesc("create_time");
    }
}
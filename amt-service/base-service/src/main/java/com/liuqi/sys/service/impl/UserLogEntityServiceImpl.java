package com.liuqi.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.liuqi.common.base.service.AbstractSimpleEntityService;
import com.liuqi.sys.bean.dto.UserLogDTO;
import com.liuqi.sys.bean.query.UserLogQuery;
import com.liuqi.sys.domain.entity.UserLogEntity;
import com.liuqi.sys.domain.mapper.UserLogMapper;
import com.liuqi.sys.service.UserLogEntityService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * 用户操作日志服务实现 
 * @author Coder Generator 2025-06-05 11:49:39 
 **/
@Service
public class UserLogEntityServiceImpl extends AbstractSimpleEntityService<UserLogEntity, UserLogDTO, UserLogMapper, UserLogQuery> implements UserLogEntityService {
    @Override
    protected QueryWrapper<UserLogEntity> queryToWrapper(UserLogQuery query) {
        return this.createQueryWrapper()
                .eq(StringUtils.isNotBlank(query.getId()), "id", query.getId())
                .in(null != query.getIds(), "id", query.getIds())
                .orderByDesc("create_time");
    }
}
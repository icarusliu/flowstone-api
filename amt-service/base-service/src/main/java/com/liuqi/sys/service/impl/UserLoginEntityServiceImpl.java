package com.liuqi.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.liuqi.common.base.service.AbstractBaseEntityService;
import com.liuqi.sys.bean.dto.UserLoginDTO;
import com.liuqi.sys.bean.query.UserLoginQuery;
import com.liuqi.sys.domain.entity.UserLoginEntity;
import com.liuqi.sys.domain.mapper.UserLoginMapper;
import com.liuqi.sys.service.UserLoginEntityService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 用户登录日志服务实现 
 * @author Coder Generator 2025-06-05 09:00:04 
 **/
@Service
public class UserLoginEntityServiceImpl extends AbstractBaseEntityService<UserLoginEntity, UserLoginDTO, UserLoginMapper, UserLoginQuery> implements UserLoginEntityService {
    @Override
    protected QueryWrapper<UserLoginEntity> queryToWrapper(UserLoginQuery query) {
        return this.createQueryWrapper()
                .eq(StringUtils.isNotBlank(query.getId()), "id", query.getId())
                .in(null != query.getIds(), "id", query.getIds())
                .orderByDesc("create_time");
    }

    /**
     * 查询用户最后一次登录日志
     *
     * @param userIds 用户id列表
     * @return 每个用户最后一条登录记录
     */
    @Override
    public List<UserLoginDTO> userLastLog(List<String> userIds) {
        return baseMapper.userLastLog(userIds);
    }
}
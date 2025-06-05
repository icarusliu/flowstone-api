package com.liuqi.sys.service;

import com.liuqi.common.base.service.BaseService;
import com.liuqi.sys.bean.dto.UserLoginDTO;
import com.liuqi.sys.bean.query.UserLoginQuery;

import java.util.List;

/**
 * 用户登录日志服务接口 
 * @author Coder Generator 2025-06-05 09:00:04 
 **/
public interface UserLoginEntityService extends BaseService<UserLoginDTO, UserLoginQuery> {
    /**
     * 查询用户最后一次登录日志
     * @param userIds 用户id列表
     * @return 每个用户最后一条登录记录
     */
    List<UserLoginDTO> userLastLog(List<String> userIds);
}
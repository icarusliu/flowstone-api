package com.liuqi.sys.service;

import com.liuqi.common.base.service.BaseService;
import com.liuqi.sys.bean.dto.UserLogDTO;
import com.liuqi.sys.bean.query.UserLogQuery;
import org.springframework.scheduling.annotation.Async;

/**
 * 用户操作日志服务接口 
 * @author Coder Generator 2025-06-05 11:49:39 
 **/
public interface UserLogEntityService extends BaseService<UserLogDTO, UserLogQuery> {
    @Async
    default void insertAsync(UserLogDTO dto) {
        this.insert(dto);
    }
}
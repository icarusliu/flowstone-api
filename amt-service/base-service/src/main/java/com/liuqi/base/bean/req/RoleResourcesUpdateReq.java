package com.liuqi.base.bean.req;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 根据角色更新角色资源列表
 */
@Data
public class RoleResourcesUpdateReq {
    private String roleId;
    private Map<String, List<String>> resourceIds;
}
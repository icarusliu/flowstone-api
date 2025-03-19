package com.liuqi.sys.bean.query;

import lombok.Data;

import java.util.List;

/**
 * 无角色用户查询对象
 *
 * @author  LiuQi 2025/3/19-9:53
 * @version V1.0
 **/
@Data
public class NoRoleUserQuery {
    private String roleId;
    private List<String> deptIds;
    private String key;
    private Long pageNo;
    private Long pageSize;
}

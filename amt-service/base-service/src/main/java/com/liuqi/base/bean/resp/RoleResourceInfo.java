package com.liuqi.base.bean.resp;

import com.liuqi.common.base.bean.dto.TreeNode;
import lombok.Data;

import java.util.List;

/**
 * 角色菜单及按钮信息
 * 用于角色管理中配置角色相关菜单按钮权限
 *
 * @author  LiuQi 2024/9/30-11:14
 * @version V1.0
 **/
@Data
public class RoleResourceInfo extends TreeNode<RoleResourceInfo> {
    /**
     * 是否选中
     */
    private Boolean checked;

    /**
     * 资源id
     */
    private String id;

    /**
     * 资源名称
     */
    private String name;

    /**
     * 按钮信息
     */
    private List<RoleResourceInfo> buttons;
}

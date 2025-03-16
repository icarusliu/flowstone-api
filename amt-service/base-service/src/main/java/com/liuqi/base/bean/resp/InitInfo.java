package com.liuqi.base.bean.resp;

import com.liuqi.base.bean.dto.MenuDTO;
import com.liuqi.base.bean.dto.UserDTO;
import lombok.Data;

import java.util.List;

/**
 * 初始化信息
 *
 * @author  LiuQi 2024/10/4-9:28
 * @version V1.0
 **/
@Data
public class InitInfo {
    private UserDTO userInfo;
    private List<MenuDTO> menuTree;
}

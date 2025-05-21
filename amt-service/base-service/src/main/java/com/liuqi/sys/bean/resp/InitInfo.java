package com.liuqi.sys.bean.resp;

import com.ibm.icu.impl.number.MacroProps;
import com.liuqi.sys.bean.dto.MenuDTO;
import com.liuqi.sys.bean.dto.SysConfigDTO;
import com.liuqi.sys.bean.dto.UserDTO;
import lombok.Data;

import java.util.List;
import java.util.Map;

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
    private Map<String, String> configInfo;
}

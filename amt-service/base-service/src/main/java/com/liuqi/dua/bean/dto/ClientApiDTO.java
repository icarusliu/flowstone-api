package com.liuqi.dua.bean.dto;

import com.liuqi.common.base.bean.dto.BaseDTO;
import lombok.Data;

/**
 * 客户端接口列表数据实体 
 * @author Coder Generator 2024-09-25 15:58:18 
 **/
@Data
public class ClientApiDTO extends BaseDTO {
    private String clientId;
    private String apiId;
}
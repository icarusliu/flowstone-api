package com.liuqi.dua.bean.req;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

/**
 * 客户端接口列表更新对象 
 * @author Coder Generator 2024-09-25 15:58:18 
 **/
@Data
public class ClientApiDeleteReq {
    private String clientId;
    private List<String> apiIds;
}